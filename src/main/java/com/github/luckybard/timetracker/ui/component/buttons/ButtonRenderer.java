package com.github.luckybard.timetracker.ui.component.buttons;

import javax.swing.*;
import java.awt.*;

public class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        if (value != null && value.equals("Sent")) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
        return this;
    }
}
