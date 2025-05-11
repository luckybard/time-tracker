package com.github.luckybard.timetracker.ui.table;

import com.github.luckybard.timetracker.util.Dictionary;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? StringUtils.EMPTY : value.toString());
        setEnabled(value == null || (!value.equals(Dictionary.translate("sent")) && !value.equals(Dictionary.translate("alreadySent"))));
        return this;
    }
}
