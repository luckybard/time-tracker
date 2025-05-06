package com.github.luckybard.timetracker.service;

import com.esotericsoftware.kryo.kryo5.util.Null;
import com.github.luckybard.timetracker.model.PluginProperties;
import com.github.luckybard.timetracker.model.Session;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.luckybard.timetracker.util.TimeUtils.getDurationAsString;

@Service(Service.Level.PROJECT)
public final class ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    private final SessionService sessionService;

    public ExcelService(@Null Project project) {
        this.sessionService = project.getService(SessionService.class);
    }

    public void promptUserAndExport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Excel File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }
            try {
                exportSessionsToExcel(sessionService.getSessions(), fileToSave);
                JOptionPane.showMessageDialog(null, "Export completed successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error during export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportSessionsToExcel(List<Session> sessions, File file) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sessions");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Branch");
        headerRow.createCell(2).setCellValue("Name");
        headerRow.createCell(3).setCellValue("Description");
        headerRow.createCell(4).setCellValue("Date");
        headerRow.createCell(5).setCellValue("Start Time");
        headerRow.createCell(6).setCellValue("End Time");
        headerRow.createCell(7).setCellValue("Duration");

        Map<LocalDate, Map<String, Duration>> dailyBranchTime = new HashMap<>();
        Map<LocalDate, Duration> dailyTotalTime = new HashMap<>();
        Map<String, Duration> weeklyBranchTime = new HashMap<>();

        int rowNum = 1;
        for (Session session : sessions) {
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

        addStatistics(sheet, dailyBranchTime, dailyTotalTime, weeklyBranchTime);

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    private void addStatistics(Sheet sheet, Map<LocalDate, Map<String, Duration>> dailyBranchTime,
                               Map<LocalDate, Duration> dailyTotalTime, Map<String, Duration> weeklyBranchTime) {
        int rowNum = sheet.getLastRowNum() + 2;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Daily Summary");
        headerRow.createCell(1).setCellValue("Branch");
        headerRow.createCell(2).setCellValue("Time Spent");
        headerRow.createCell(3).setCellValue("Percentage of Day");

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
        weeklySummaryHeader.createCell(0).setCellValue("Weekly Summary");
        weeklySummaryHeader.createCell(1).setCellValue("Branch");
        weeklySummaryHeader.createCell(2).setCellValue("Total Time Spent");

        for (Map.Entry<String, Duration> branchEntry : weeklyBranchTime.entrySet()) {
            String branch = branchEntry.getKey();
            Duration timeSpent = branchEntry.getValue();

            Row row = sheet.createRow(rowNum++);
            row.createCell(1).setCellValue(branch);
            row.createCell(2).setCellValue(getDurationAsString(timeSpent));
        }
    }
}
