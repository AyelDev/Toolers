package com.toolkit.toolers.Services;

import com.toolkit.toolers.BaseFrame;
import com.toolkit.toolers.Dialog.ImageTaskPanel;
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
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.api.errors.TransportException;
import java.net.URI;

import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;
import java.util.Set;
import java.util.HashSet;

public class DirectoryServices {

    private static final Set<String> activeFetches = java.util.Collections.synchronizedSet(new HashSet<>());

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
                                    "Ready", new JButton("open")
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

                String fullPath = model.getValueAt(modelRow, 2).toString();

                if (!activeFetches.add(fullPath)) {
                    JOptionPane.showMessageDialog(
                            Table,
                            "Fetch is already running for this repository.");
                    return;
                }

                if (Table.isEditing()) {
                    Table.getCellEditor().stopCellEditing();
                }

                fetchInBackground(Table, fullPath, null);
            }

            private String extractTokenFromUrl(String url) {
                if (url == null)
                    return null;
                try {
                    URI uri = new URI(url);
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && !userInfo.isEmpty()) {
                        return userInfo;
                    }
                } catch (Exception e) {
                    // URL parsing failed (e.g. SSH URLs)
                }
                return null;
            }

            @Override
            public void onOpen(int row) {
                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();

                Object fullPath = model.getValueAt(modelRow, 2);

                try {
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
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Table, "Error opening folder: " + e.getMessage());
                }
            }

        };
        Table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        Table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(event));
        Table.setRowHeight(50);
    }

    public void GetTaskDirectoryData(JTable Table, String filterMode) {
        DefaultTableModel model = (DefaultTableModel) Table.getModel();
        model.setRowCount(0);

        String appDir = ConfigService.loadConfig().getProperty("app.dir");
        Path taskRoot = Paths.get(appDir, "Task");

        if (!Files.exists(taskRoot)) {
            try {
                Files.createDirectories(taskRoot);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String todayPrefix = getTodayPrefix();

        try (var taskStream = Files.list(taskRoot)) {
            taskStream.filter(Files::isDirectory).forEach(taskDir -> {
                String taskName = taskDir.getFileName().toString();

                try (var clientStream = Files.list(taskDir)) {
                    clientStream.filter(Files::isDirectory).forEach(clientDir -> {
                        String clientName = clientDir.getFileName().toString();
                        String fullPath = clientDir.toAbsolutePath().toString();

                        if ("Today".equals(filterMode)) {
                            boolean hasToday = false;
                            try (var dateStream = Files.list(clientDir)) {
                                hasToday = dateStream
                                        .filter(Files::isDirectory)
                                        .anyMatch(dateDir -> dateDir.getFileName().toString().startsWith(todayPrefix));
                            } catch (IOException e) {
                                // ignore
                            }
                            if (!hasToday)
                                return;
                        }

                        try {
                            BasicFileAttributes attrs = Files.readAttributes(clientDir, BasicFileAttributes.class);
                            Date lastModified = new Date(attrs.lastModifiedTime().toMillis());
                            model.addRow(new Object[] {
                                    taskName,
                                    clientName,
                                    fullPath,
                                    new JButton("open")
                            });
                        } catch (IOException e) {
                            System.out.println("Could not read attributes for: " + clientDir);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException ex) {
            System.getLogger(DirectoryServices.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        com.toolkit.toolers.cell.TableTaskActionEvent event = new com.toolkit.toolers.cell.TableTaskActionEvent() {
            @Override
            public void onCreateTask(int row) {
                // TODO: implement later
                System.out.println("Create Task clicked for row: " + row);

                ImageTaskPanel panel = new ImageTaskPanel();
                javax.swing.JDialog dialog = new javax.swing.JDialog();
                dialog.setTitle("Image Task");
                dialog.setModal(false);
                dialog.setContentPane(panel);
                dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                dialog.pack();
                dialog.setLocationRelativeTo(Table);
                dialog.setVisible(true);

            }

            @Override
            public void onAddImage(int row) {
                // TODO: implement later
                System.out.println("Add Image clicked for row: " + row);
            }

            @Override
            public void onOpenDirectory(int row) {
                int modelRow = Table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) Table.getModel();
                Object fullPath = model.getValueAt(modelRow, 2);

                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        new ProcessBuilder("explorer.exe", fullPath.toString()).start();
                    } else if (os.contains("mac")) {
                        new ProcessBuilder("open", fullPath.toString()).start();
                    } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                        new ProcessBuilder("xdg-open", fullPath.toString()).start();
                    } else {
                        JOptionPane.showMessageDialog(Table, "Unsupported operating system: " + os);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Table, "Error opening folder: " + e.getMessage());
                }
            }
        };
        Table.getColumnModel().getColumn(3).setCellRenderer(new com.toolkit.toolers.cell.TableTaskActionCellRender());
        Table.getColumnModel().getColumn(3)
                .setCellEditor(new com.toolkit.toolers.cell.TableTaskActionCellEditor(event));
        Table.setRowHeight(50);
    }

    private String getTodayPrefix() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        String monthStr = monthFormat.format(calendar.getTime());
        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);
        return monthStr + "-" + day + "-" + year + "-";
    }

    private boolean isSSHUrl(String url) {
        return url != null &&
                (url.startsWith("git@")
                        || url.startsWith("ssh://"));
    }

    private void fetchInBackground(
            JTable table,
            String fullPath,
            String replacementUrl) {

        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {

                try (Git git = Git.open(new File(fullPath))) {

                    String remoteUrl = git.getRepository()
                            .getConfig()
                            .getString("remote", "origin", "url");

                    if (remoteUrl == null) {
                        throw new IOException("No origin remote configured.");
                    }

                    var fetch = git.fetch().setTimeout(5);

                    if (replacementUrl != null) {

                        // Fetch using new SSH URL.
                        // Do not save the new URL yet.
                        fetch.setRemote(replacementUrl);

                        // Preserve the existing origin fetch mappings.
                        String[] specs = git.getRepository()
                                .getConfig()
                                .getStringList("remote", "origin", "fetch");

                        if (specs.length > 0) {
                            java.util.List<org.eclipse.jgit.transport.RefSpec> refSpecs = new java.util.ArrayList<>();

                            for (String spec : specs) {
                                refSpecs.add(
                                        new org.eclipse.jgit.transport.RefSpec(spec));
                            }

                            fetch.setRefSpecs(refSpecs);
                        }

                    } else if (isHTTPSUrl(remoteUrl)) {

                        String token = ConfigService.getProperty(
                                "token." + fullPath);

                        if (token != null && !token.isBlank()) {
                            fetch.setCredentialsProvider(
                                    new UsernamePasswordCredentialsProvider(
                                            "x-access-token",
                                            token));
                        }
                    }

                    fetch.call();

                    // Save new SSH URL only after successful fetch.
                    if (replacementUrl != null) {
                        var config = git.getRepository().getConfig();

                        config.setString(
                                "remote",
                                "origin",
                                "url",
                                replacementUrl);

                        config.save();
                    }
                }

                return null;
            }

            @Override
            protected void done() {

                try {
                    get();

                    activeFetches.remove(fullPath);

                    JOptionPane.showMessageDialog(
                            table,
                            "Fetch completed successfully!",
                            "Git Fetch",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {

                    Throwable cause = ex;

                    if (ex instanceof ExecutionException
                            && ex.getCause() != null) {
                        cause = ex.getCause();
                    }

                    if (replacementUrl == null
                            && cause instanceof TransportException) {

                        try (Git git = Git.open(new File(fullPath))) {

                            String remoteUrl = git.getRepository()
                                    .getConfig()
                                    .getString("remote", "origin", "url");

                            if (isSSHUrl(remoteUrl)) {

                                promptNewSSH(table, fullPath, cause);

                            } else if (isHTTPSUrl(remoteUrl)) {

                                promptHTTPSCredentials(table, fullPath, cause);

                            } else {

                                activeFetches.remove(fullPath);

                                JOptionPane.showMessageDialog(
                                        table,
                                        "Unsupported Git remote URL.",
                                        "Fetch Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                        } catch (Exception e) {
                            activeFetches.remove(fullPath);

                            JOptionPane.showMessageDialog(
                                    table,
                                    e.getMessage(),
                                    "Git Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    } else {

                        activeFetches.remove(fullPath);

                        JOptionPane.showMessageDialog(
                                table,
                                "Fetch failed:\n" + cause.getMessage(),
                                "Git Fetch Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        worker.execute();
    }

    private void promptNewSSH(
            JTable table,
            String fullPath,
            Throwable error) {
        try (Git git = Git.open(new File(fullPath))) {

            String oldUrl = git.getRepository()
                    .getConfig()
                    .getString("remote", "origin", "url");

            if (!isSSHUrl(oldUrl)) {
                activeFetches.remove(fullPath);

                JOptionPane.showMessageDialog(
                        table,
                        "Fetch failed:\n" + error.getMessage(),
                        "Fetch Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    table,
                    "Unable to connect to SSH:\n"
                            + oldUrl
                            + "\n\nError: " + error.getMessage()
                            + "\n\nDo you want to update the SSH URL?",
                    "Update SSH",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                activeFetches.remove(fullPath);
                return;
            }

            String newUrl = JOptionPane.showInputDialog(
                    table,
                    "Enter new SSH URL:",
                    oldUrl);

            if (newUrl == null) {
                activeFetches.remove(fullPath);
                return;
            }

            newUrl = newUrl.trim();

            if (!isSSHUrl(newUrl) || newUrl.equals(oldUrl)) {
                activeFetches.remove(fullPath);

                JOptionPane.showMessageDialog(
                        table,
                        "Please enter a valid and different SSH URL.",
                        "Invalid SSH URL",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Retry in another SwingWorker
            fetchInBackground(table, fullPath, newUrl);

        } catch (Exception ex) {
            activeFetches.remove(fullPath);

            JOptionPane.showMessageDialog(
                    table,
                    "Error: " + ex.getMessage(),
                    "Git Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isHTTPSUrl(String url) {
        return url != null &&
                (url.startsWith("https://")
                        || url.startsWith("http://"));
    }

    private void promptHTTPSCredentials(
            JTable table,
            String fullPath,
            Throwable error) {

        javax.swing.JPasswordField tokenField = new javax.swing.JPasswordField(25);

        int result = JOptionPane.showConfirmDialog(
                table,
                tokenField,
                "Enter GitHub Personal Access Token",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            activeFetches.remove(fullPath);
            return;
        }

        char[] password = tokenField.getPassword();

        try {
            String token = new String(password);

            if (token.isBlank()) {
                activeFetches.remove(fullPath);
                return;
            }

            // Save token for subsequent fetches.
            ConfigService.setProperty(
                    "token." + fullPath,
                    token);

            fetchInBackground(table, fullPath, null);

        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }

}
