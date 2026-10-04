package com.seep.algorithm;

import com.seep.model.Road;
import com.seep.model.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Builds a weighted graph from the list of Road edges and computes the
 * safest/shortest evacuation route between two locations using
 * Dijkstra's Algorithm.
 *
 * Blocked roads are excluded entirely from the graph; congested or
 * hazardous roads are simply penalised via Road.getWeight() so that safer
 * (even if slightly longer) routes are preferred automatically.
 */
public class DijkstraSolver {

    /** adjacency list: node -> list of (neighbour, weight) edges */
    private final Map<String, List<Edge>> graph = new HashMap<>();

    public DijkstraSolver(List<Road> roads) {
        buildGraph(roads);
    }

    private void buildGraph(List<Road> roads) {
        for (Road road : roads) {
            if (!road.isPassable()) {
                continue; // skip blocked roads entirely
            }
            graph.computeIfAbsent(road.getSource(), k -> new ArrayList<>())
                    .add(new Edge(road.getDestination(), road.getWeight()));
            // Treat roads as usable in both directions for evacuation purposes
            graph.computeIfAbsent(road.getDestination(), k -> new ArrayList<>())
                    .add(new Edge(road.getSource(), road.getWeight()));
        }
    }

    /**
     * Computes the lowest-cost path from source to destination.
     */
    public Route findShortestPath(String source, String destination) {
        if (!graph.containsKey(source) || !graph.containsKey(destination)) {
            return Route.notFound();
        }

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>();

        for (String node : graph.keySet()) {
            dist.put(node, Double.MAX_VALUE);
        }
        dist.put(source, 0.0);
        queue.add(new NodeDistance(source, 0.0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();

            if (visited.contains(current.node)) {
                continue;
            }
            visited.add(current.node);

            if (current.node.equals(destination)) {
                break;
            }

            for (Edge edge : graph.getOrDefault(current.node, Collections.emptyList())) {
                if (visited.contains(edge.to)) {
                    continue;
                }
                double newDist = dist.get(current.node) + edge.weight;
                if (newDist < dist.get(edge.to)) {
                    dist.put(edge.to, newDist);
                    previous.put(edge.to, current.node);
                    queue.add(new NodeDistance(edge.to, newDist));
                }
            }
        }

        if (!dist.containsKey(destination) || dist.get(destination) == Double.MAX_VALUE) {
            return Route.notFound();
        }

        // Reconstruct path by walking backwards through "previous" links
        List<String> path = new ArrayList<>();
        String step = destination;
        while (step != null) {
            path.add(0, step);
            step = previous.get(step);
        }

        if (path.isEmpty() || !path.get(0).equals(source)) {
            return Route.notFound();
        }

        return new Route(path, dist.get(destination), true);
    }

    public Set<String> getAllNodes() {
        return graph.keySet();
    }

    /** Simple edge record used internally by the adjacency list. */
    private static class Edge {
        final String to;
        final double weight;

        Edge(String to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /** Wrapper used by the PriorityQueue to order nodes by current distance. */
    private static class NodeDistance implements Comparable<NodeDistance> {
        final String node;
        final double distance;

        NodeDistance(String node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
