package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class to load and manage application configuration from config.properties
 */
public class ConfigLoader {
    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());
    private static ConfigLoader instance;
    private final Properties properties;

    private static final String CONFIG_FILE = "config.properties";

    private ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    public static ConfigLoader getInstance() {
        if (instance == null) {
            synchronized (ConfigLoader.class) {
                if (instance == null) {
                    instance = new ConfigLoader();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            LOGGER.info("✓ Configuration loaded successfully from " + CONFIG_FILE);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "⚠ Configuration file not found, loading defaults", e);
            loadDefaultProperties();
        }
    }

    private void loadDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/LibroNova");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("diasPrestamo", "7");
        properties.setProperty("multaPorDia", "1500");
        properties.setProperty("app.name", "LibroNova");
        properties.setProperty("log.file", "app.log");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid integer property: " + key, e);
            return defaultValue;
        }
    }

    public double getDoubleProperty(String key, double defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid double property: " + key, e);
            return defaultValue;
        }
    }

    public int getLoanDays() {
        return getIntProperty("diasPrestamo", 7);
    }

    public double getFinePerDay() {
        return getDoubleProperty("multaPorDia", 1500.0);
    }
}