package com.toolkit.toolers.cell;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class TableTaskActionCellEditor extends DefaultCellEditor {

    private TableTaskActionEvent event;

    public TableTaskActionCellEditor(TableTaskActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        PanelTaskAction action = new PanelTaskAction();
        action.initEvent(event, row);
        action.setBackground(table.getSelectionBackground());
        return action;
    }
}
