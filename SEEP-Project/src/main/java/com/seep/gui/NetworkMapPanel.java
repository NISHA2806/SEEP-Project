package com.seep.gui;

import com.seep.model.Road;
import com.seep.model.Shelter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Draws the evacuation road network as a simple graph: locations as
 * nodes (shelters highlighted), roads as colour-coded edges, and the
 * currently recommended route highlighted in blue.
 */
public class NetworkMapPanel extends JPanel {

    private List<Road> roads = new ArrayList<>();
    private List<Shelter> shelters = new ArrayList<>();
    private List<String> highlightedPath = Collections.emptyList();
    private final Map<String, Point> nodePositions = new HashMap<>();

    public NetworkMapPanel() {
        setPreferredSize(new Dimension(420, 420));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Evacuation Network Map"));
    }

    public void setData(List<Road> roads, List<Shelter> shelters, List<String> highlightedPath) {
        this.roads = roads != null ? roads : Collections.emptyList();
        this.shelters = shelters != null ? shelters : Collections.emptyList();
        this.highlightedPath = highlightedPath != null ? highlightedPath : Collections.emptyList();
        computeLayout();
        repaint();
    }

    private void computeLayout() {
        Set<String> nodes = new TreeSet<>();
        for (Road r : roads) {
            nodes.add(r.getSource());
            nodes.add(r.getDestination());
        }
        nodePositions.clear();
        int w = Math.max(getWidth(), 420);
        int h = Math.max(getHeight(), 420);
        int cx = w / 2;
        int cy = h / 2;
        int radius = Math.min(w, h) / 2 - 70;
        int n = Math.max(nodes.size(), 1);
        int i = 0;
        for (String node : nodes) {
            double angle = 2 * Math.PI * i / n;
            int x = cx + (int) (radius * Math.cos(angle));
            int y = cy + (int) (radius * Math.sin(angle));
            nodePositions.put(node, new Point(x, y));
            i++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (nodePositions.isEmpty()) {
            computeLayout();
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw all roads first
        for (Road r : roads) {
            Point p1 = nodePositions.get(r.getSource());
            Point p2 = nodePositions.get(r.getDestination());
            if (p1 == null || p2 == null) continue;

            Color c;
            switch (r.getStatus().toUpperCase()) {
                case "BLOCKED": c = new Color(200, 40, 40); break;
                case "CONGESTED": c = new Color(230, 150, 30); break;
                default: c = new Color(70, 160, 70);
            }
            g2.setColor(c);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // Draw the recommended route on top, thicker and blue
        if (highlightedPath.size() > 1) {
            g2.setColor(new Color(30, 90, 220));
            g2.setStroke(new BasicStroke(4f));
            for (int idx = 0; idx < highlightedPath.size() - 1; idx++) {
                Point p1 = nodePositions.get(highlightedPath.get(idx));
                Point p2 = nodePositions.get(highlightedPath.get(idx + 1));
                if (p1 != null && p2 != null) {
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        Set<String> shelterLocations = new HashSet<>();
        for (Shelter s : shelters) {
            shelterLocations.add(s.getLocation());
        }

        // Draw nodes on top of edges
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            Point p = entry.getValue();
            boolean isShelter = shelterLocations.contains(entry.getKey());
            int size = isShelter ? 18 : 12;

            g2.setColor(isShelter ? new Color(30, 120, 200) : new Color(90, 90, 90));
            g2.fillOval(p.x - size / 2, p.y - size / 2, size, size);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - size / 2, p.y - size / 2, size, size);
            g2.drawString(entry.getKey(), p.x + 10, p.y + 4);
        }

        drawLegend(g2);
    }

    private void drawLegend(Graphics2D g2) {
        int x = 10;
        int y = getHeight() - 95;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

        legendRow(g2, x, y, new Color(70, 160, 70), "Clear road");
        legendRow(g2, x, y += 16, new Color(230, 150, 30), "Congested road");
        legendRow(g2, x, y += 16, new Color(200, 40, 40), "Blocked road");
        legendRow(g2, x, y += 16, new Color(30, 90, 220), "Recommended route");
        legendRow(g2, x, y += 16, new Color(30, 120, 200), "Shelter");
    }

    private void legendRow(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillRect(x, y, 12, 12);
        g2.setColor(Color.BLACK);
        g2.drawString(label, x + 18, y + 10);
    }
}
