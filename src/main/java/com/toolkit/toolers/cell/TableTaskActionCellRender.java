package com.toolkit.toolers.cell;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableTaskActionCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        PanelTaskAction action = new PanelTaskAction();

        if (!isSelected && row % 2 == 0) {
            action.setBackground(Color.WHITE);
        } else if (!isSelected) {
            action.setBackground(new Color(242, 242, 242));
        } else {
            action.setBackground(com.getBackground());
        }

        return action;
    }
}
