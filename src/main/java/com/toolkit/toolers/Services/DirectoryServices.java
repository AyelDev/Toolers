package com.toolkit.toolers.Services;

import com.toolkit.toolers.BaseFrame;
import com.toolkit.toolers.cell.TableActionCellEditor;
import com.toolkit.toolers.cell.TableActionCellRender;
import com.toolkit.toolers.cell.TableActionEvent;
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
                            model.addRow(new Object[]{p.getFileName(), sdf.format(lastModified), p.toAbsolutePath(), new JButton("open")
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
                
                if(Table.isEditing()){
                    Table.getCellEditor().cancelCellEditing();
                }
                
                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();

                Object folderName = model.getValueAt(modelRow, 0);
                Object fullPath = model.getValueAt(modelRow, 2);

                int confirm = JOptionPane.showConfirmDialog(Table, "Do you want to delete " + folderName.toString() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

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

//                System.out.println("Deleting: " + folderName + " at " + fullPath);
//                model.removeRow(modelRow);
            }

            @Override
            public void onView(int row) {
                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();

                Object fullPath = model.getValueAt(modelRow, 2);
                System.out.println("Viewing: " + fullPath);
            }
        };
        Table.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());
        Table.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor(event));
        Table.setRowHeight(50);
    }

}
