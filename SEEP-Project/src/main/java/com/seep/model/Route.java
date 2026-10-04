package com.seep.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a route computation: an ordered list of
 * locations from source to destination, together with the total
 * effective distance/cost.
 */
public class Route {

    private final List<String> path;
    private final double totalCost;
    private final boolean found;

    public Route(List<String> path, double totalCost, boolean found) {
        this.path = path;
        this.totalCost = totalCost;
        this.found = found;
    }

    public static Route notFound() {
        return new Route(Collections.emptyList(), -1, false);
    }

    public List<String> getPath() {
        return path;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public boolean isFound() {
        return found;
    }

    @Override
    public String toString() {
        if (!found) {
            return "No safe route could be found between the selected locations.";
        }
        return String.join(" -> ", path) + String.format("  (effective cost: %.2f)", totalCost);
    }
}
