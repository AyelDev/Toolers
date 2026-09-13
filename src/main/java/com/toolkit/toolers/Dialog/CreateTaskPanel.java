/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.toolkit.toolers.Dialog;

import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;

import com.toolkit.toolers.Services.ConfigService;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

/**
 *
 * @author Ariel
 */
public class CreateTaskPanel extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger
            .getLogger(CreateTaskPanel.class.getName());
    private boolean disableShortcuts = false;

    /**
     * Creates new form CreateTaskPanel
     */
    public CreateTaskPanel() {
        initComponents();
        javax.swing.InputMap im = jPanel1.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        javax.swing.ActionMap am = jPanel1.getActionMap();

        im.put(javax.swing.KeyStroke.getKeyStroke("1"), "toggleGit");
            am.put("toggleGit", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (disableShortcuts) return;
                GitScreenshotTxt.setSelected(!GitScreenshotTxt.isSelected());
                GitPB.setValue(GitScreenshotTxt.isSelected() ? 100 : 0);
                GitPB.setStringPainted(true);
                GitPB.setString(GitScreenshotTxt.isSelected() ? "Ready" : "");
            }
        });

        im.put(javax.swing.KeyStroke.getKeyStroke("2"), "togglePage");
            am.put("togglePage", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (disableShortcuts) return;
                PageScreenshotsTxt.setSelected(!PageScreenshotsTxt.isSelected());
                PageScreenBP.setValue(PageScreenshotsTxt.isSelected() ? 100 : 0);
                PageScreenBP.setStringPainted(true);
                PageScreenBP.setString(PageScreenshotsTxt.isSelected() ? "Ready" : "");
            }
        });

        im.put(javax.swing.KeyStroke.getKeyStroke("3"), "toggleTest");
            am.put("toggleTest", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (disableShortcuts) return;
                TestScreenshotsTxt.setSelected(!TestScreenshotsTxt.isSelected());
                TestScreenPB.setValue(TestScreenshotsTxt.isSelected() ? 100 : 0);
                TestScreenPB.setStringPainted(true);
                TestScreenPB.setString(TestScreenshotsTxt.isSelected() ? "Ready" : "");
            }
        });

        im.put(javax.swing.KeyStroke.getKeyStroke("4"), "toggleAttach");
            am.put("toggleAttach", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (disableShortcuts) return;
                AttachmentTxt.setSelected(!AttachmentTxt.isSelected());
                AttachmentPB.setValue(AttachmentTxt.isSelected() ? 100 : 0);
                AttachmentPB.setStringPainted(true);
                AttachmentPB.setString(AttachmentTxt.isSelected() ? "Ready" : "");
            }
        });

        im.put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "close");
        am.put("close", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (disableShortcuts) return;
                
                if(selectTaskCmbo.getSelectedItem() == null || selectTaskCmbo.getSelectedItem().toString().isEmpty()){
                    JOptionPane.showMessageDialog(CreateTaskPanel.this, "Please select a task before closing.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(selectClientCmbo.getSelectedItem() == null || selectClientCmbo.getSelectedItem().toString().isEmpty()){
                    JOptionPane.showMessageDialog(CreateTaskPanel.this, "Please select a client before closing.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(!GitScreenshotTxt.isSelected() && !PageScreenshotsTxt.isSelected() && !TestScreenshotsTxt.isSelected() && !AttachmentTxt.isSelected()){
                    JOptionPane.showMessageDialog(CreateTaskPanel.this, "Please select at least one option before closing.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                //create directory
                
                Properties props = ConfigService.loadConfig();
                String appDir = props.getProperty("app.dir");
                Path root = Paths.get(appDir, "Task");
                props.setProperty("app.taskDir", root.toString());
                ConfigService.saveConfig();

                

                String taskDir = root.toString() + "\\" + selectTaskCmbo.getSelectedItem().toString() + "\\" + selectClientCmbo.getSelectedItem().toString().trim() + "\\" + getDateTime() + props.getProperty("app.user") ;
                File dir = new File(taskDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if(GitScreenshotTxt.isSelected()){
                        new File(taskDir, "Git Screenshot").mkdirs();
                }

                if(PageScreenshotsTxt.isSelected()){
                        File pageDir = new File(taskDir, "Page Screenshots");
                        pageDir.mkdirs();
                        
                        new File(pageDir, "Before").mkdirs();
                        new File(pageDir, "After").mkdirs();
                        
                }

                if(TestScreenshotsTxt.isSelected()){
                        new File(taskDir, "Test Screenshots").mkdirs();
                }

                if(AttachmentTxt.isSelected()){
                        new File(taskDir, "Attachment(s)").mkdirs();
                }

                dispose();
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loadTasks();
        loadClients();
        getRootPane().setDefaultButton(null);
    }

    private String getDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        String monthStr = monthFormat.format(calendar.getTime()); // e.g. "September"

        int day = calendar.get(Calendar.DATE);
        String dayStr = String.valueOf(day); // e.g. "2"

        int year = calendar.get(Calendar.YEAR);
        String yearStr = String.valueOf(year); // e.g. "2026"

        return monthStr + "-" + dayStr + "-" + yearStr + "-";
    }

    private void loadTasks() {
        selectTaskCmbo.removeAllItems();
        List<String> tasks = ConfigService.getTasks();
        for (String task : tasks) {
            selectTaskCmbo.addItem(task);
        }
    }

    private void loadClients() {
        selectClientCmbo.removeAllItems();
        List<String> clients = ConfigService.getClients();
        for (String client : clients) {
            selectClientCmbo.addItem(client);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        GitScreenshotTxt = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        PageScreenshotsTxt = new javax.swing.JCheckBox();
        TestScreenshotsTxt = new javax.swing.JCheckBox();
        AttachmentTxt = new javax.swing.JCheckBox();
        selectTaskCmbo = new javax.swing.JComboBox<>();
        newTaskTxt = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        GitPB = new javax.swing.JProgressBar();
        PageScreenBP = new javax.swing.JProgressBar();
        TestScreenPB = new javax.swing.JProgressBar();
        AttachmentPB = new javax.swing.JProgressBar();
        DeleteBtn = new javax.swing.JButton();
        AddNewTaskBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        deleteClientBtn = new javax.swing.JButton();
        addClientBtn = new javax.swing.JButton();
        selectClientCmbo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        GitScreenshotTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        GitScreenshotTxt.setText("Git Screenshot [1]");
        GitScreenshotTxt.addActionListener(this::GitScreenshotTxtActionPerformed);

        PageScreenshotsTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        PageScreenshotsTxt.setText("Page Screenshots [2]");

        TestScreenshotsTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TestScreenshotsTxt.setText("Test Screenshots [3]");

        AttachmentTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        AttachmentTxt.setText("Attachment(s) [4]");

        selectTaskCmbo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        newTaskTxt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        newTaskTxt.setText(" ");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Create Task Folder");

        GitPB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        PageScreenBP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        TestScreenPB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        AttachmentPB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        DeleteBtn.setText("Delete");
        DeleteBtn.addActionListener(this::DeleteBtnActionPerformed);

        AddNewTaskBtn.setText("Add New Task");
        AddNewTaskBtn.addActionListener(this::AddNewTaskBtnActionPerformed);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Press Enter to Close");

        deleteClientBtn.setText("Delete");
        deleteClientBtn.addActionListener(this::DeleteClientBtnActionPerformed);

        addClientBtn.setText("Add New Client");
        addClientBtn.addActionListener(this::AddNewClientBtnActionPerformed);

        selectClientCmbo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(GitScreenshotTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(PageScreenshotsTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TestScreenshotsTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(AttachmentTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(TestScreenPB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                                    .addComponent(GitPB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(PageScreenBP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(AttachmentPB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(DeleteBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AddNewTaskBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(selectTaskCmbo, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(selectClientCmbo, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteClientBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addClientBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectTaskCmbo, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AddNewTaskBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addClientBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteClientBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectClientCmbo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(GitPB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GitScreenshotTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PageScreenBP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PageScreenshotsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TestScreenPB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TestScreenshotsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AttachmentPB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AttachmentTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GitScreenshotTxtActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_GitScreenshotTxtActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_GitScreenshotTxtActionPerformed

    private void AddNewTaskBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_AddNewTaskBtnActionPerformed
        // TODO add your handling code here:
        String taskName = JOptionPane.showInputDialog(
                this, "Enter task name", "Add Task", JOptionPane.PLAIN_MESSAGE);
        if (taskName == null || taskName.trim().isEmpty())
            return;

        ConfigService.addTask(taskName.trim());
        loadTasks();
        selectTaskCmbo.setSelectedItem(taskName.trim());
    }// GEN-LAST:event_AddNewTaskBtnActionPerformed

    private void DeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_DeleteBtnActionPerformed
        // TODO add your handling code here:
        String selectedTask = (String) selectTaskCmbo.getSelectedItem();
        if (selectedTask == null)
            return;

        ConfigService.removeTask(selectedTask);
        loadTasks();
    }// GEN-LAST:event_DeleteBtnActionPerformed

    private void AddNewClientBtnActionPerformed(java.awt.event.ActionEvent evt) {
        String clientName = JOptionPane.showInputDialog(
                this, "Enter client name", "Add Client", JOptionPane.PLAIN_MESSAGE);
        if (clientName == null || clientName.trim().isEmpty())
            return;

        ConfigService.addClient(clientName.trim());
        loadClients();
        selectClientCmbo.setSelectedItem(clientName.trim());
    }

    private void DeleteClientBtnActionPerformed(java.awt.event.ActionEvent evt) {
        String selectedClient = (String) selectClientCmbo.getSelectedItem();
        if (selectedClient == null)
            return;

        ConfigService.removeClient(selectedClient);
        loadClients();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddNewTaskBtn;
    private javax.swing.JProgressBar AttachmentPB;
    private javax.swing.JCheckBox AttachmentTxt;
    private javax.swing.JButton DeleteBtn;
    private javax.swing.JProgressBar GitPB;
    private javax.swing.JCheckBox GitScreenshotTxt;
    private javax.swing.JProgressBar PageScreenBP;
    private javax.swing.JCheckBox PageScreenshotsTxt;
    private javax.swing.JProgressBar TestScreenPB;
    private javax.swing.JCheckBox TestScreenshotsTxt;
    private javax.swing.JButton addClientBtn;
    private javax.swing.JButton deleteClientBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField newTaskTxt;
    private javax.swing.JComboBox<String> selectClientCmbo;
    private javax.swing.JComboBox<String> selectTaskCmbo;
    // End of variables declaration//GEN-END:variables
}
