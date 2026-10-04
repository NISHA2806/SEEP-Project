package com.seep.gui;

import com.seep.algorithm.DijkstraSolver;
import com.seep.dao.RoadDAO;
import com.seep.dao.ShelterDAO;
import com.seep.exception.SEEPException;
import com.seep.model.Road;
import com.seep.model.Route;
import com.seep.model.Shelter;
import com.seep.util.AppLogger;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Lets a citizen pick a current location and find the safest evacuation
 * route to the nearest available shelter, using Dijkstra's Algorithm.
 * Shows the result both as text and as a visual network map.
 */
public class RouteFinderPanel extends JPanel {

    private final JComboBox<String> sourceBox = new JComboBox<>();
    private final JComboBox<String> destinationBox = new JComboBox<>();
    private final JTextArea resultArea = new JTextArea(12, 30);
    private final NetworkMapPanel mapPanel = new NetworkMapPanel();
    private final RoadDAO roadDAO = new RoadDAO();
    private final ShelterDAO shelterDAO = new ShelterDAO();

    public RouteFinderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Current Location:"), gbc);
        gbc.gridx = 1;
        formPanel.add(sourceBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Destination (optional):"), gbc);
        gbc.gridx = 1;
        formPanel.add(destinationBox, gbc);

        JButton findNearestBtn = new JButton("Find Safest Route to Nearest Shelter");
        JButton findToDestBtn = new JButton("Find Safest Route to Selected Destination");
        JButton refreshBtn = new JButton("Refresh Locations");

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.add(findNearestBtn);
        buttonPanel.add(findToDestBtn);
        buttonPanel.add(refreshBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(resultArea), mapPanel);
        splitPane.setResizeWeight(0.4);

        add(formPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        findNearestBtn.addActionListener(e -> findRouteToNearestShelter());
        findToDestBtn.addActionListener(e -> findRouteToDestination());
        refreshBtn.addActionListener(e -> loadLocations());

        loadLocations();
    }

    private void loadLocations() {
        try {
            Set<String> locations = new TreeSet<>(roadDAO.getAllLocations());
            sourceBox.removeAllItems();
            destinationBox.removeAllItems();
            for (String loc : locations) {
                sourceBox.addItem(loc);
                destinationBox.addItem(loc);
            }
            resultArea.setText("Locations loaded. Select a starting point and press a route button.\n");

            List<Road> roads = roadDAO.getAllRoads();
            List<Shelter> shelters = shelterDAO.getAllShelters();
            mapPanel.setData(roads, shelters, null);
        } catch (SEEPException e) {
            showError(e.getMessage());
        }
    }

    private void findRouteToNearestShelter() {
        String source = (String) sourceBox.getSelectedItem();
        if (source == null) {
            showError("No locations available. Please refresh.");
            return;
        }

        try {
            List<Road> roads = roadDAO.getAllRoads();
            List<Shelter> shelters = shelterDAO.getAllShelters();
            DijkstraSolver solver = new DijkstraSolver(roads);

            Route best = null;
            Shelter bestShelter = null;

            for (Shelter shelter : shelters) {
                if (!shelter.hasSpace()) {
                    continue;
                }
                Route route = solver.findShortestPath(source, shelter.getLocation());
                if (route.isFound() && (best == null || route.getTotalCost() < best.getTotalCost())) {
                    best = route;
                    bestShelter = shelter;
                }
            }

            if (best == null) {
                resultArea.setText("No reachable shelter with available space was found from '" + source + "'.\n");
                mapPanel.setData(roads, shelters, null);
                AppLogger.warn("No reachable shelter found from " + source);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Nearest available shelter: ").append(bestShelter.getName())
                        .append(" (").append(bestShelter.getLocation()).append(")\n");
                sb.append("Available space: ").append(bestShelter.getAvailableSpace())
                        .append(" / ").append(bestShelter.getCapacity()).append("\n\n");
                sb.append("Recommended route:\n").append(best).append("\n");
                resultArea.setText(sb.toString());
                mapPanel.setData(roads, shelters, best.getPath());
                AppLogger.info("Route computed from " + source + " to shelter " + bestShelter.getName());
            }
        } catch (SEEPException e) {
            showError(e.getMessage());
        }
    }

    private void findRouteToDestination() {
        String source = (String) sourceBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();

        if (source == null || destination == null) {
            showError("Please select both a source and a destination.");
            return;
        }
        if (source.equals(destination)) {
            resultArea.setText("Source and destination are the same location.\n");
            return;
        }

        try {
            List<Road> roads = roadDAO.getAllRoads();
            List<Shelter> shelters = shelterDAO.getAllShelters();
            DijkstraSolver solver = new DijkstraSolver(roads);
            Route route = solver.findShortestPath(source, destination);
            resultArea.setText("Route from " + source + " to " + destination + ":\n" + route + "\n");
            mapPanel.setData(roads, shelters, route.isFound() ? route.getPath() : null);
        } catch (SEEPException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        resultArea.setText("Error: " + message + "\n");
        AppLogger.warn(message);
    }
}
