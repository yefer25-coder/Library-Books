package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class to manage database connections
 */
public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;

    private final String URL;
    private final String USER;
    private final String PASSWORD;

    private DatabaseConnection() {
        ConfigLoader config = ConfigLoader.getInstance();
        this.URL = config.getProperty("db.url");
        this.USER = config.getProperty("db.user");
        this.PASSWORD = config.getProperty("db.password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("✓ MySQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "✗ MySQL Driver not found", e);
            throw new RuntimeException("Database driver not found", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Get a shared connection (reuses the same connection)
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("✓ Database connection established");
        }
        return connection;
    }

    /**
     * Get a new independent connection (for transactions)
     */
    public Connection getNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        LOGGER.fine("✓ New database connection created");
        return conn;
    }

}