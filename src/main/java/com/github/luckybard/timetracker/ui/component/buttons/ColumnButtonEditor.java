package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.DefaultCellEditor;
import java.awt.*;

public abstract class ColumnButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private String label;
    private boolean isClicked;
    private final BranchTimeTrackerService trackerService;
    public final Runnable reloadTable;

    // Konstruktor
    public ColumnButtonEditor(JCheckBox checkBox, String label, BranchTimeTrackerService trackerService, Runnable reloadTable) {
        super(checkBox);
        this.button = new JButton();
        this.button.setOpaque(true);
        this.trackerService = trackerService;
        this.reloadTable = reloadTable;
        this.label = label;
        this.button.setText(label);
        this.button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.label = (value == null) ? "" : value.toString();
        this.isClicked = true;

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isClicked) {
            handleButtonClick(getTable().getSelectedRow());
        }
        isClicked = false;
        return label;
    }

    public abstract void handleButtonClick(int row);

    public JBTable getTable() {
        return (JBTable) SwingUtilities.getAncestorOfClass(JBTable.class, this.button);
    }

    public BranchTimeTrackerService getTrackerService() {
        return trackerService;
    }

    public Runnable getReloadTable() {
        return reloadTable;
    }

    public boolean confirmAction(String message, String title) {
        int confirm = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return confirm == JOptionPane.YES_OPTION;
    }
}
