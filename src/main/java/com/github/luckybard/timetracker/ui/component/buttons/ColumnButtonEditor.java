package com.github.luckybard.timetracker.ui.component.buttons;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public abstract class ColumnButtonEditor extends DefaultCellEditor {
    protected final JButton button;
    private String label;
    private boolean isClicked;
    public Project project;

    public ColumnButtonEditor(@NotNull Project project, JCheckBox checkBox, String label) {
        super(checkBox);
        this.project = project;
        this.button = new JButton();
        this.button.setOpaque(true);
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

    public boolean confirmAction(String message, String title) {
        int confirm = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return confirm == JOptionPane.YES_OPTION;
    }
}
