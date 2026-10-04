package com.seep.dao;

import com.seep.exception.SEEPException;
import com.seep.model.Road;
import com.seep.util.AppLogger;
import com.seep.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Data-access object for the roads table (edges of the evacuation graph).
 */
public class RoadDAO {

    public List<Road> getAllRoads() throws SEEPException {
        List<Road> roads = new ArrayList<>();
        String sql = "SELECT * FROM roads ORDER BY road_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                roads.add(mapRow(rs));
            }
        } catch (SQLException e) {
            AppLogger.error("Failed to fetch roads", e);
            throw new SEEPException("Could not load road data.", e);
        }
        return roads;
    }

    /** Returns the distinct set of location names used across all roads. */
    public Set<String> getAllLocations() throws SEEPException {
        Set<String> locations = new LinkedHashSet<>();
        for (Road r : getAllRoads()) {
            locations.add(r.getSource());
            locations.add(r.getDestination());
        }
        return locations;
    }

    public void addRoad(Road road) throws SEEPException {
        String sql = "INSERT INTO roads (source, destination, distance_km, status, hazard_level) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, road.getSource());
            ps.setString(2, road.getDestination());
            ps.setDouble(3, road.getDistanceKm());
            ps.setString(4, road.getStatus());
            ps.setString(5, road.getHazardLevel());
            ps.executeUpdate();
            AppLogger.info("Added road: " + road);
        } catch (SQLException e) {
            AppLogger.error("Failed to add road", e);
            throw new SEEPException("Could not add road.", e);
        }
    }

    public void updateRoadStatus(int roadId, String status, String hazardLevel) throws SEEPException {
        String sql = "UPDATE roads SET status = ?, hazard_level = ? WHERE road_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, hazardLevel);
            ps.setInt(3, roadId);
            ps.executeUpdate();
            AppLogger.info("Updated road #" + roadId + " -> status=" + status + ", hazard=" + hazardLevel);
        } catch (SQLException e) {
            AppLogger.error("Failed to update road status", e);
            throw new SEEPException("Could not update road status.", e);
        }
    }

    public void deleteRoad(int roadId) throws SEEPException {
        String sql = "DELETE FROM roads WHERE road_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roadId);
            ps.executeUpdate();
            AppLogger.info("Deleted road #" + roadId);
        } catch (SQLException e) {
            AppLogger.error("Failed to delete road", e);
            throw new SEEPException("Could not delete road.", e);
        }
    }

    private Road mapRow(ResultSet rs) throws SQLException {
        return new Road(
                rs.getInt("road_id"),
                rs.getString("source"),
                rs.getString("destination"),
                rs.getDouble("distance_km"),
                rs.getString("status"),
                rs.getString("hazard_level")
        );
    }
}
