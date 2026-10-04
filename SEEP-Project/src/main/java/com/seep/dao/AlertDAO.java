package com.seep.dao;

import com.seep.exception.SEEPException;
import com.seep.util.AppLogger;
import com.seep.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the alerts table.
 */
public class AlertDAO {

    public void addAlert(String message, String severity) throws SEEPException {
        String sql = "INSERT INTO alerts (message, severity) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, message);
            ps.setString(2, severity);
            ps.executeUpdate();
            AppLogger.info("New alert issued [" + severity + "]: " + message);
        } catch (SQLException e) {
            AppLogger.error("Failed to add alert", e);
            throw new SEEPException("Could not issue alert.", e);
        }
    }

    public List<String> getAllAlerts() throws SEEPException {
        List<String> alerts = new ArrayList<>();
        String sql = "SELECT message, severity, created_at FROM alerts ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alerts.add(String.format("[%s] %s - %s",
                        rs.getString("severity"), rs.getTimestamp("created_at"), rs.getString("message")));
            }
        } catch (SQLException e) {
            AppLogger.error("Failed to fetch alerts", e);
            throw new SEEPException("Could not load alerts.", e);
        }
        return alerts;
    }
}
