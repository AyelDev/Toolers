package com.toolkit.toolers.Services;

import com.toolkit.toolers.BaseFrame;
import com.toolkit.toolers.cell.TableActionCellEditor;
import com.toolkit.toolers.cell.TableActionCellRender;
import com.toolkit.toolers.cell.TableActionEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.eclipse.jgit.api.Git;

public class DirectoryServices {

    public void GetDirectoryData(JTable Table) {
        DefaultTableModel model = (DefaultTableModel) Table.getModel();
        model.setRowCount(0);

        String appDir = ConfigService.loadConfig().getProperty("app.dir");
        Path root = Paths.get(appDir, "Repo");

        try {
            Files.createDirectories(root);
            ConfigService.saveConfig();

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();

        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (var stream = Files.list(root)) {
            stream.filter(Files::isDirectory)
                    .forEach(p -> {
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
                            Date lastModified = new Date(attrs.lastModifiedTime().toMillis());
                            model.addRow(new Object[] { p.getFileName(), sdf.format(lastModified), p.toAbsolutePath(),
                                    new JButton("open")
                            });

                        } catch (IOException e) {
                            System.out.println("Could not read attributes for: " + p);
                        }
                    });
        } catch (IOException ex) {
            System.getLogger(BaseFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onDelete(int row) {

                if (Table.isEditing()) {
                    Table.getCellEditor().cancelCellEditing();
                }

                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();

                Object folderName = model.getValueAt(modelRow, 0);
                Object fullPath = model.getValueAt(modelRow, 2);

                int confirm = JOptionPane.showConfirmDialog(Table,
                        "Do you want to delete " + folderName.toString() + "?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Path directory = Paths.get(fullPath.toString());

                        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                file.toFile().setWritable(true);
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                dir.toFile().setWritable(true);
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                        model.removeRow(modelRow);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Table, "Error deleting the folder: " + ex.getMessage());
                    }

                }

                // System.out.println("Deleting: " + folderName + " at " + fullPath);
                // model.removeRow(modelRow);
            }

            @Override
            public void onTerminal(int row) {
                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();

                Object fullPath = model.getValueAt(modelRow, 2);

                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        // Windows
                        new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/K",
                                "cd /d \"" + fullPath.toString() + "\"").start();
                    } else if (os.contains("mac")) {
                        // macOS
                        new ProcessBuilder("open", "-a", "Terminal", fullPath.toString()).start();
                    } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                        // Linux/Unix
                        new ProcessBuilder("x-terminal-emulator", "-e", "bash", "-c",
                                "cd \"" + fullPath.toString() + "\"; exec bash").start();
                    } else {
                        JOptionPane.showMessageDialog(Table, "Unsupported operating system: " + os);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Table, "Error opening terminal: " + e.getMessage());

                }
            }
            
            @Override
            public void onFetch(int row) {
                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();

                Object fullPath = model.getValueAt(modelRow, 2);

                File repoDir = new File(fullPath.toString());

                try (Git git = Git.open(repoDir)) {
                    git.fetch().call();
                    JOptionPane.showMessageDialog(Table, "Fetch completed successfully for: " + fullPath.toString());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(Table, "Error opening Git repository: " + e.getMessage());
                } catch (Exception e) {
                    Table.getCellEditor().cancelCellEditing();
                    JOptionPane.showMessageDialog(Table, "Error fetching data: " + e.getMessage());
                } 
             
            }
            
            @Override
            public void onOpen(int row) {
                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();

                Object fullPath = model.getValueAt(modelRow, 2);

               try{
                     String os = System.getProperty("os.name").toLowerCase();
                     if (os.contains("win")) {
                          // Windows
                          new ProcessBuilder("explorer.exe", fullPath.toString()).start();
                     } else if (os.contains("mac")) {
                          // macOS
                          new ProcessBuilder("open", fullPath.toString()).start();
                     } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                          // Linux/Unix
                          new ProcessBuilder("xdg-open", fullPath.toString()).start();
                     } else {
                          JOptionPane.showMessageDialog(Table, "Unsupported operating system: " + os);
                     }
               }catch(Exception e){
                   JOptionPane.showMessageDialog(Table, "Error opening folder: " + e.getMessage());
               }
            }

             
        };
        Table.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());
        Table.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor(event));
        Table.setRowHeight(50);
    }

}
