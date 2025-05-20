package com.github.luckybard.timetracker.controller;

import com.esotericsoftware.kryo.kryo5.util.Null;
import com.github.luckybard.timetracker.service.ExportService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static com.github.luckybard.timetracker.util.Dictionary.COLON_WITH_SPACE;
import static com.github.luckybard.timetracker.util.Dictionary.translate;

@Service(Service.Level.PROJECT)
public final class ExcelController {

    private final ExportService exportService;

    public ExcelController(@Null Project project) {
        this.exportService = project.getService(ExportService.class);
    }

    public void promptUserAndExport() {
        File file = promptUserForFile();
        if (file != null) {
            try {
                exportService.prepareFIle(file);
                JOptionPane.showMessageDialog(null, translate("excel.file.export.success"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, translate("excel.export.error") +
                        COLON_WITH_SPACE + ex.getMessage(), translate("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private File promptUserForFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(translate("excel.save.file"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                return new File(file.getAbsolutePath() + ".xlsx");
            }
            return file;
        }
        return null;
    }
}
