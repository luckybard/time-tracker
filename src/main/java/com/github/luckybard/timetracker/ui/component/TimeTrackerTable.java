package com.github.luckybard.timetracker.ui.component;

import com.github.luckybard.timetracker.model.Session;
import com.github.luckybard.timetracker.service.BranchTimeTrackerService;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.util.List;

public class TimeTrackerTable {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final BranchTimeTrackerService trackerService;

    public TimeTrackerTable(Project project) {
        this.trackerService = project.getService(BranchTimeTrackerService.class);
        tableModel = new DefaultTableModel(new String[]{"ID", "Branch", "Date", "Start Time", "End Time", "Duration", "Send to Jira", "Edit", "Delete"}, 0);
        table = new JBTable(tableModel);

        initializeButtons();
        updateTable();
    }

    public void updateTable() {
        clearTable();
        List<Session> sessions = trackerService.getSessionStorage().getSessions();
        for (Session session : sessions) {
            tableModel.addRow(new Object[]{
                    session.getId(),
                    session.getBranch(),
                    session.getDate(),
                    session.getStartTime(),
                    session.getEndTime(),
                    formatDuration(session.getDuration()),
                    session.isSentToJira() ? "Sent to Jira" : "Send to Jira",  // Dynamicznie zmieniamy tekst
                    "Edit",  // Przycisk "Edit"
                    "Delete" // Przycisk "Delete"
            });
        }
    }

    private void initializeButtons() {
        table.getColumn("Send to Jira").setCellRenderer(new ButtonRenderer());
        table.getColumn("Send to Jira").setCellEditor(new ColumnButtonEditor(new JCheckBox(), "Send to Jira", trackerService, this::updateTable));

        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new ColumnButtonEditor(new JCheckBox(), "Edit", trackerService, this::updateTable));

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ColumnButtonEditor(new JCheckBox(), "Delete", trackerService, this::updateTable));
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }

    public JScrollPane getTableScrollPane() {
        return new JBScrollPane(table);
    }

    // Renderer dla przycisków (w kolumnach)
    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            if (value != null && value.equals("Sent to Jira")) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
            return this;
        }
    }

    private class ColumnButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isClicked;
        private final BranchTimeTrackerService trackerService;
        private final Runnable reloadTable;

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
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (column == table.getColumn("Edit").getModelIndex()) {
                    editSession(row);
                } else if (column == table.getColumn("Delete").getModelIndex()) {
                    if(confirmAction("Czy na pewno chcesz usunąć?", "Usuwanie")){
                        deleteSession(row);
                    }
                } else if (column == table.getColumn("Send to Jira").getModelIndex()) {
                    if(confirmAction("Czy na pewno chcesz wysłać?", "Wysłanie")){
                        sendToJira(row);
                    }
                }
            }
            isClicked = false;
            return label; // Zwracamy etykietę przycisku
        }

        private void sendToJira(int row) {
            if (row < 0 || row >= tableModel.getRowCount()) return;

            String sessionId = (String) tableModel.getValueAt(row, 0);
            Session session = trackerService.getSessionStorage().getSessions().stream()
                    .filter(s -> s.getId().equals(sessionId))
                    .findFirst()
                    .orElse(null);

            if (session != null && !session.isSentToJira()) {
                boolean success = trackerService.sendSessionToJira(session);
                if (success) {
                    session.setSentToJira(true); // Ustawienie statusu sesji na wysłaną do JIRA
                    updateTable(); // Odświeżenie tabeli, aby zaktualizować stan
                } else {
                    JOptionPane.showMessageDialog(null, "Błąd podczas wysyłania do JIRA.");
                }
            }
        }

        // Edycja sesji
        private void editSession(int row) {
            if (row < 0 || row >= tableModel.getRowCount()) return;

            String sessionId = (String) tableModel.getValueAt(row, 0);
            Session session = trackerService.getSessionStorage().getSessions().stream()
                    .filter(s -> s.getId().equals(sessionId))
                    .findFirst()
                    .orElse(null);

            if (session != null) {
                JTextField branchField = new JTextField(session.getBranch());
                JTextField dateField = new JTextField(session.getDate().toString());
                JTextField startTimeField = new JTextField(session.getStartTime().toString());
                JTextField endTimeField = new JTextField(session.getEndTime().toString());

                JPanel panel = new JPanel(new GridLayout(4, 2));
                panel.add(new JLabel("Branch:"));
                panel.add(branchField);
                panel.add(new JLabel("Date:"));
                panel.add(dateField);
                panel.add(new JLabel("Start Time:"));
                panel.add(startTimeField);
                panel.add(new JLabel("End Time:"));
                panel.add(endTimeField);

                int result = JOptionPane.showConfirmDialog(null, panel,
                        "Edit Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    session.setBranch(branchField.getText());
                    session.setDate(dateField.getText());
                    session.setStartTime(startTimeField.getText());
                    session.setEndTime(endTimeField.getText());
                    reloadTable.run();
                }
            }
        }

        // Usuwanie sesji
        private void deleteSession(int row) {
            if (row < 0 || row >= tableModel.getRowCount()) return;

            String sessionId = (String) tableModel.getValueAt(row, 0);

            trackerService.getSessionStorage().getSessions()
                    .removeIf(s -> s.getId().equals(sessionId));

            SwingUtilities.invokeLater(() -> {
                tableModel.removeRow(row);
                tableModel.fireTableStructureChanged();
                initializeButtons();
                table.repaint();
            });
        }
    }

    public boolean confirmAction(String message, String title) {
        int confirm = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return confirm == JOptionPane.YES_OPTION;
    }

}
