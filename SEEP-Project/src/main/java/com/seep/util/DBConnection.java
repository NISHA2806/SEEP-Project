package com.seep.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Handles loading of database configuration and creation of JDBC
 * connections to the MySQL database.
 */
public final class DBConnection {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties props;

    private DBConnection() {
        // utility class - no instances
    }

    private static synchronized Properties loadProperties() throws IOException {
        if (props != null) {
            return props;
        }
        props = new Properties();

        // Try loading from the project root first (works when run from VS Code / java -cp)
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
            AppLogger.info("Loaded config.properties from working directory.");
            return props;
        } catch (IOException fileNotFound) {
            // Fall back to classpath resource (works when packaged inside the jar)
            try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (in == null) {
                    throw new IOException("config.properties not found on disk or classpath.");
                }
                props.load(in);
                AppLogger.info("Loaded config.properties from classpath.");
                return props;
            }
        }
    }

    /**
     * Opens and returns a new JDBC connection using the settings from
     * config.properties.
     *
     * @throws SQLException if the connection could not be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            Properties p = loadProperties();
            String url = p.getProperty("db.url");
            String user = p.getProperty("db.user");
            String password = p.getProperty("db.password");

            // Explicitly loading the driver keeps older JDBC tooling happy.
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found on classpath.", e);
        } catch (IOException e) {
            throw new SQLException("Could not read database configuration.", e);
        }
    }
}
