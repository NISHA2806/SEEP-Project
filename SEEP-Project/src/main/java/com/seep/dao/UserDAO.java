package com.seep.dao;

import com.seep.exception.SEEPException;
import com.seep.model.Admin;
import com.seep.model.User;
import com.seep.util.AppLogger;
import com.seep.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data-access object for the users table. Handles authentication and
 * registration of both citizen users and administrators.
 */
public class UserDAO {

    /**
     * Attempts to authenticate a user. Returns an Admin instance if the
     * matched row has role = ADMIN (Polymorphism: callers can treat the
     * result as a plain User without caring which subclass it is).
     *
     * @throws SEEPException if the credentials are invalid
     */
    public User login(String username, String password) throws SEEPException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Admin(rs.getInt("user_id"), rs.getString("username"),
                                rs.getString("password"), rs.getString("full_name"));
                    } else {
                        return new User(rs.getInt("user_id"), rs.getString("username"),
                                rs.getString("password"), rs.getString("full_name"), role);
                    }
                } else {
                    throw new SEEPException("Invalid username or password.");
                }
            }
        } catch (SQLException e) {
            AppLogger.error("Login query failed", e);
            throw new SEEPException("Unable to reach the database. Please try again later.", e);
        }
    }

    /**
     * Registers a new citizen user.
     *
     * @throws SEEPException if the username is already taken or the insert fails
     */
    public void register(String username, String password, String fullName) throws SEEPException {
        String checkSql = "SELECT user_id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, 'USER')";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, username);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        throw new SEEPException("Username '" + username + "' is already taken.");
                    }
                }
            }

            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setString(1, username);
                insert.setString(2, password);
                insert.setString(3, fullName);
                insert.executeUpdate();
                AppLogger.info("Registered new user: " + username);
            }

        } catch (SQLException e) {
            AppLogger.error("Registration failed", e);
            throw new SEEPException("Registration failed due to a database error.", e);
        }
    }
}
