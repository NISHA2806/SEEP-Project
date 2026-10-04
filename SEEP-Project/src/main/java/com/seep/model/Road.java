package com.seep.model;

/**
 * Represents a road segment (edge) connecting two locations (nodes) in the
 * evacuation graph.
 */
public class Road {

    public static final String STATUS_CLEAR = "CLEAR";
    public static final String STATUS_CONGESTED = "CONGESTED";
    public static final String STATUS_BLOCKED = "BLOCKED";

    private int roadId;
    private String source;
    private String destination;
    private double distanceKm;
    private String status;
    private String hazardLevel; // LOW, MEDIUM, HIGH

    public Road() {
    }

    public Road(int roadId, String source, String destination, double distanceKm,
                String status, String hazardLevel) {
        this.roadId = roadId;
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.status = status;
        this.hazardLevel = hazardLevel;
    }

    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public boolean isPassable() {
        return !STATUS_BLOCKED.equalsIgnoreCase(status);
    }

    /**
     * Effective "weight" used by the routing algorithm. Congested/hazardous
     * roads are penalised so that Dijkstra naturally prefers safer paths.
     */
    public double getWeight() {
        double weight = distanceKm;

        if (STATUS_CONGESTED.equalsIgnoreCase(status)) {
            weight *= 1.5;
        }

        if ("MEDIUM".equalsIgnoreCase(hazardLevel)) {
            weight *= 1.3;
        } else if ("HIGH".equalsIgnoreCase(hazardLevel)) {
            weight *= 2.0;
        }

        return weight;
    }

    @Override
    public String toString() {
        return source + " -> " + destination + " (" + distanceKm + " km, "
                + status + ", hazard: " + hazardLevel + ")";
    }
}
