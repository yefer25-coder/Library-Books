package config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 * Custom logger configuration for application logs and HTTP simulation
 */
public class LoggerConfig {
    private static final Logger APP_LOGGER = Logger.getLogger("AppLogger");
    private static final Logger HTTP_LOGGER = Logger.getLogger("HttpLogger");
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Configure file handler for app.log
            String logFile = ConfigLoader.getInstance().getProperty("log.file", "app.log");
            FileHandler fileHandler = new FileHandler(logFile, true);
            fileHandler.setFormatter(new CustomLogFormatter());

            APP_LOGGER.addHandler(fileHandler);
            APP_LOGGER.setLevel(Level.ALL);
            APP_LOGGER.setUseParentHandlers(false);

            // Configure console handler for HTTP simulation logs
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new HttpLogFormatter());
            consoleHandler.setLevel(Level.INFO);

            HTTP_LOGGER.addHandler(consoleHandler);
            HTTP_LOGGER.setLevel(Level.INFO);
            HTTP_LOGGER.setUseParentHandlers(false);

            initialized = true;
            APP_LOGGER.info("✓ Logger configuration initialized successfully");

        } catch (IOException e) {
            System.err.println("✗ Error configuring logger: " + e.getMessage());
        }
    }

    public static Logger getAppLogger() {
        if (!initialized) {
            initialize();
        }
        return APP_LOGGER;
    }

    public static Logger getHttpLogger() {
        if (!initialized) {
            initialize();
        }
        return HTTP_LOGGER;
    }

    /**
     * Log HTTP-style request (simulated)
     */
    public static void logHttpRequest(String method, String endpoint, String user) {
        HTTP_LOGGER.info(String.format("[%s] %s - User: %s", method, endpoint, user != null ? user : "anonymous"));
    }

    /**
     * Log HTTP-style response (simulated)
     */
    public static void logHttpResponse(int statusCode, String message) {
        String status = statusCode >= 200 && statusCode < 300 ? "✓" : "✗";
        HTTP_LOGGER.info(String.format("%s Response: %d - %s", status, statusCode, message));
    }

    /**
     * Log application info
     */
    public static void logInfo(String message) {
        APP_LOGGER.info(message);
    }

    /**
     * Log application warning
     */
    public static void logWarning(String message) {
        APP_LOGGER.warning(message);
    }

    /**
     * Log application error
     */
    public static void logError(String message, Throwable throwable) {
        APP_LOGGER.log(Level.SEVERE, message, throwable);
    }

    /**
     * Custom formatter for application logs
     */
    static class CustomLogFormatter extends Formatter {
        private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(LocalDateTime.now().format(DTF)).append("]");
            sb.append(" [").append(record.getLevel()).append("]");
            sb.append(" ").append(record.getMessage());

            if (record.getThrown() != null) {
                sb.append("\n").append("Exception: ").append(record.getThrown().toString());
            }

            sb.append("\n");
            return sb.toString();
        }
    }

    /**
     * Custom formatter for HTTP simulation logs
     */
    static class HttpLogFormatter extends Formatter {
        private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            return String.format("[%s] %s%n", LocalDateTime.now().format(DTF), record.getMessage());
        }
    }
}