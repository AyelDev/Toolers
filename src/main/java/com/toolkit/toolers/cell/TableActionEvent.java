/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toolkit.toolers.cell;

/**
 *
 * @author Ariel
 */
public interface TableActionEvent {
    public void onDelete(int row);
    public void onTerminal(int row);
    public void onFetch(int row);
    public void onOpen(int row);
}
