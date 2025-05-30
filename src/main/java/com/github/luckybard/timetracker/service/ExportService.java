package com.github.luckybard.timetracker.service;

import com.esotericsoftware.kryo.kryo5.util.Null;
import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.util.Dictionary;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.github.luckybard.timetracker.util.TimeUtils.getDurationAsString;

@Service(Service.Level.PROJECT)
public final class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    private final SessionService sessionService;

    public ExportService(@Null Project project) {
        this.sessionService = project.getService(SessionService.class);
    }

    public void prepareFIle(File file) throws IOException {
        logger.debug("ExportService::prepareFile()");
        Map<LocalDate, Map<String, Duration>> dailyBranchTime = new HashMap<>();
        Map<LocalDate, Duration> dailyTotalTime = new HashMap<>();
        Map<String, Duration> weeklyBranchTime = new HashMap<>();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sessions");
        addSessions(sheet, dailyBranchTime, dailyTotalTime, weeklyBranchTime);
        addStatistics(sheet, dailyBranchTime, dailyTotalTime, weeklyBranchTime);

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    private void addSessions(Sheet sheet, Map<LocalDate, Map<String, Duration>> dailyBranchTime,
                             Map<LocalDate, Duration> dailyTotalTime, Map<String, Duration> weeklyBranchTime) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(Dictionary.translate("id"));
        headerRow.createCell(1).setCellValue(Dictionary.translate("branch"));
        headerRow.createCell(2).setCellValue(Dictionary.translate("name"));
        headerRow.createCell(3).setCellValue(Dictionary.translate("description"));
        headerRow.createCell(4).setCellValue(Dictionary.translate("date"));
        headerRow.createCell(5).setCellValue(Dictionary.translate("startTime"));
        headerRow.createCell(6).setCellValue(Dictionary.translate("endTime"));
        headerRow.createCell(7).setCellValue(Dictionary.translate("duration"));

        int rowNum = 1;
        for (Session session : sessionService.getSessions()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(session.getId());
            row.createCell(1).setCellValue(session.getBranch());
            row.createCell(2).setCellValue(session.getName());
            row.createCell(3).setCellValue(session.getDescription());
            row.createCell(4).setCellValue(session.getDate());
            row.createCell(5).setCellValue(session.getStartTime());
            row.createCell(6).setCellValue(session.getEndTime());
            row.createCell(7).setCellValue(getDurationAsString(session.getDuration()));

            LocalDate sessionDate = LocalDate.parse(session.getDate());
            dailyBranchTime
                    .computeIfAbsent(sessionDate, k -> new HashMap<>())
                    .merge(session.getBranch(), session.getDuration(), Duration::plus);
            dailyTotalTime.merge(sessionDate, session.getDuration(), Duration::plus);
            weeklyBranchTime.merge(session.getBranch(), session.getDuration(), Duration::plus);
        }
    }

    private void addStatistics(Sheet sheet, Map<LocalDate, Map<String, Duration>> dailyBranchTime,
                               Map<LocalDate, Duration> dailyTotalTime, Map<String, Duration> weeklyBranchTime) {
        int rowNum = sheet.getLastRowNum() + 2;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue(Dictionary.translate("excel.daily.summary"));
        headerRow.createCell(1).setCellValue(Dictionary.translate("branch"));
        headerRow.createCell(2).setCellValue(Dictionary.translate("excel.time.spent"));
        headerRow.createCell(3).setCellValue(Dictionary.translate("excel.percentage.of.day"));

        for (Map.Entry<LocalDate, Map<String, Duration>> entry : dailyBranchTime.entrySet()) {
            LocalDate date = entry.getKey();
            for (Map.Entry<String, Duration> branchEntry : entry.getValue().entrySet()) {
                String branch = branchEntry.getKey();
                Duration timeSpent = branchEntry.getValue();
                Duration totalDayTime = dailyTotalTime.get(date);
                double percentage = (totalDayTime != null && totalDayTime.toMinutes() > 0)
                        ? (timeSpent.toMinutes() / (double) totalDayTime.toMinutes()) * 100
                        : 0;

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(date.toString());
                row.createCell(1).setCellValue(branch);
                row.createCell(2).setCellValue(getDurationAsString(timeSpent));
                row.createCell(3).setCellValue(String.format("%.2f", percentage));
            }
        }

        rowNum += 2;
        Row weeklySummaryHeader = sheet.createRow(rowNum++);
        weeklySummaryHeader.createCell(0).setCellValue(Dictionary.translate("excel.weekly.summary"));
        weeklySummaryHeader.createCell(1).setCellValue(Dictionary.translate("branch"));
        weeklySummaryHeader.createCell(2).setCellValue(Dictionary.translate("excel.total.time.spent"));

        for (Map.Entry<String, Duration> branchEntry : weeklyBranchTime.entrySet()) {
            String branch = branchEntry.getKey();
            Duration timeSpent = branchEntry.getValue();

            Row row = sheet.createRow(rowNum++);
            row.createCell(1).setCellValue(branch);
            row.createCell(2).setCellValue(getDurationAsString(timeSpent));
        }
    }
}
