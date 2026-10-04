package com.seep.dao;

import com.seep.exception.SEEPException;
import com.seep.model.Shelter;
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
 * Data-access object for the shelters table.
 * Demonstrates use of the Collections Framework (ArrayList).
 */
public class ShelterDAO {

    public List<Shelter> getAllShelters() throws SEEPException {
        List<Shelter> shelters = new ArrayList<>();
        String sql = "SELECT * FROM shelters ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                shelters.add(mapRow(rs));
            }
        } catch (SQLException e) {
            AppLogger.error("Failed to fetch shelters", e);
            throw new SEEPException("Could not load shelter data.", e);
        }
        return shelters;
    }

    public void addShelter(Shelter shelter) throws SEEPException {
        String sql = "INSERT INTO shelters (name, location, capacity, occupied) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, shelter.getName());
            ps.setString(2, shelter.getLocation());
            ps.setInt(3, shelter.getCapacity());
            ps.setInt(4, shelter.getOccupied());
            ps.executeUpdate();
            AppLogger.info("Added shelter: " + shelter.getName());
        } catch (SQLException e) {
            AppLogger.error("Failed to add shelter", e);
            throw new SEEPException("Could not add shelter.", e);
        }
    }

    public void updateOccupancy(int shelterId, int newOccupied) throws SEEPException {
        String sql = "UPDATE shelters SET occupied = ? WHERE shelter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newOccupied);
            ps.setInt(2, shelterId);
            ps.executeUpdate();
            AppLogger.info("Updated occupancy for shelter #" + shelterId + " to " + newOccupied);
        } catch (SQLException e) {
            AppLogger.error("Failed to update shelter occupancy", e);
            throw new SEEPException("Could not update shelter occupancy.", e);
        }
    }

    public void deleteShelter(int shelterId) throws SEEPException {
        String sql = "DELETE FROM shelters WHERE shelter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, shelterId);
            ps.executeUpdate();
            AppLogger.info("Deleted shelter #" + shelterId);
        } catch (SQLException e) {
            AppLogger.error("Failed to delete shelter", e);
            throw new SEEPException("Could not delete shelter.", e);
        }
    }

    private Shelter mapRow(ResultSet rs) throws SQLException {
        return new Shelter(
                rs.getInt("shelter_id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getInt("capacity"),
                rs.getInt("occupied")
        );
    }
}
