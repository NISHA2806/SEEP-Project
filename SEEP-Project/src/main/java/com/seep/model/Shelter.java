package com.seep.model;

/**
 * Represents an evacuation shelter. The "location" field also acts as a
 * node name in the road graph used by the Dijkstra route-finding algorithm.
 */
public class Shelter {

    private int shelterId;
    private String name;
    private String location;
    private int capacity;
    private int occupied;

    public Shelter() {
    }

    public Shelter(int shelterId, String name, String location, int capacity, int occupied) {
        this.shelterId = shelterId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.occupied = occupied;
    }

    public int getShelterId() {
        return shelterId;
    }

    public void setShelterId(int shelterId) {
        this.shelterId = shelterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public void setOccupied(int occupied) {
        this.occupied = occupied;
    }

    public int getAvailableSpace() {
        return capacity - occupied;
    }

    public boolean hasSpace() {
        return getAvailableSpace() > 0;
    }

    @Override
    public String toString() {
        return name + " [" + location + "] - Available: " + getAvailableSpace() + "/" + capacity;
    }
}
