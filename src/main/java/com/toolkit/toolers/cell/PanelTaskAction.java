package com.toolkit.toolers.cell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelTaskAction extends javax.swing.JPanel {

    public PanelTaskAction() {
        initComponents();
    }

    public void initEvent(TableTaskActionEvent event, int row) {
        cmdAddImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                event.onAddImage(row);
            }
        });

        cmdCreateTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                event.onCreateTask(row);
            }
        });

        cmdDirectoryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                event.onOpenDirectory(row);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmdAddImage = new com.toolkit.toolers.cell.ActionButton();
        cmdCreateTask = new com.toolkit.toolers.cell.ActionButton();
        cmdDirectoryBtn = new javax.swing.JButton();

        cmdAddImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/toolkit/toolers/images/add.png"))); // NOI18N

        cmdCreateTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/toolkit/toolers/images/screenshot.png"))); // NOI18N

        cmdDirectoryBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/toolkit/toolers/images/folder.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdAddImage, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmdCreateTask, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdDirectoryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(cmdAddImage, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(cmdCreateTask, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cmdDirectoryBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toolkit.toolers.cell.ActionButton cmdAddImage;
    private com.toolkit.toolers.cell.ActionButton cmdCreateTask;
    private javax.swing.JButton cmdDirectoryBtn;
    // End of variables declaration//GEN-END:variables
}
