package com.toolkit.toolers.cell;

public interface TableTaskActionEvent {
    public void onCreateTask(int row);
    public void onAddImage(int row);
    public void onOpenDirectory(int row);
}
