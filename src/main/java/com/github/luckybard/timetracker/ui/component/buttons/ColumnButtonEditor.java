package com.github.luckybard.timetracker.ui.component.buttons;

import com.github.luckybard.timetracker.service.SessionService;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;

public abstract class ColumnButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private String label;
    private boolean isClicked;
    public final Runnable reloadTable;
    protected SessionService sessionService;
 
    public ColumnButtonEditor(SessionService sessionService, JCheckBox checkBox, String label, Runnable reloadTable) {
        super(checkBox);
        this.sessionService = sessionService;
        this.button = new JButton();
        this.button.setOpaque(true);
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

    public Runnable getReloadTable() {
        return reloadTable;
    }

    public boolean confirmAction(String message, String title) {
        int confirm = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return confirm == JOptionPane.YES_OPTION;
    }
}
