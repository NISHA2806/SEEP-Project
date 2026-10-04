package com.seep;

import com.seep.gui.LoginFrame;
import com.seep.util.AppLogger;
import com.seep.util.DBConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Application entry point for the Smart Emergency Evacuation Planner (SEEP).
 *
 * Before launching the GUI, performs a quick database connectivity check
 * so the user gets a clear error message if MySQL is not running or the
 * config.properties values are wrong, instead of a confusing stack trace.
 */
public class Main {

    public static void main(String[] args) {
        AppLogger.info("Starting Smart Emergency Evacuation Planner...");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            AppLogger.warn("Could not set system look and feel, using default.");
        }

        SwingUtilities.invokeLater(() -> {
            if (!checkDatabaseConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to the MySQL database.\n\n"
                                + "Please make sure:\n"
                                + "  1. MySQL server is running\n"
                                + "  2. The 'seep_db' database has been created (see database/seep_db.sql)\n"
                                + "  3. config.properties has the correct URL/username/password\n\n"
                                + "The application will now exit.",
                        "Database Connection Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            new LoginFrame().setVisible(true);
        });
    }

    private static boolean checkDatabaseConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            AppLogger.info("Database connection successful.");
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            AppLogger.error("Database connection failed", e);
            return false;
        }
    }
}
