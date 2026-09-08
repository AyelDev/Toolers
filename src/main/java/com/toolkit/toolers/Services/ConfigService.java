package com.toolkit.toolers.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.Properties;

public class ConfigService {
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static Properties properties = new Properties();

    public static Properties loadConfig(String filePath) {
        String userHome = System.getProperty("user.home");
        File configFile = new File(userHome, CONFIG_FILE_NAME);

        if (!configFile.exists())
            createConfig(configFile, filePath);

        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            System.out.println("Config loaded at: " + configFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }


    public static Properties loadConfig() {

        String userHome = System.getProperty("user.home");
        File configFile = new File(userHome, CONFIG_FILE_NAME);

        if (!configFile.exists())
            createDefaultConfig(configFile);

        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            System.out.println("Config loaded at: " + configFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static void createDefaultConfig(File configFile) {
        Properties defaults = new Properties();
        defaults.setProperty("app.dir", "");
        defaults.setProperty("app.repoDir", "");
        defaults.setProperty("app.user", "");

        try (FileOutputStream output = new FileOutputStream(configFile)) {
            defaults.store(output, "Default application configuration");
            System.out.println("Created default config at: " + configFile.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createConfig(File configFile, String filePath) {
        String _userHome = System.getProperty("user.home");
        File _configFile = new File(_userHome, CONFIG_FILE_NAME);
        Properties defaults = new Properties();
        defaults.setProperty("app.dir", filePath);
        defaults.setProperty("app.repoDir", filePath);
        defaults.setProperty("app.user", "");
        

        try (FileOutputStream output = new FileOutputStream(_configFile)) {
            defaults.store(output, "Default application configuration");
            System.out.println("Created default config at: " + _configFile.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void saveConfig() {
        String userHome = System.getProperty("user.home");
        File configFile = new File(userHome, CONFIG_FILE_NAME);

        try (FileOutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, "Updated application configuration");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
