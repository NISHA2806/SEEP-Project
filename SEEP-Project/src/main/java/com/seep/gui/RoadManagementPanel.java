package com.seep.gui;

import com.seep.dao.RoadDAO;
import com.seep.exception.SEEPException;
import com.seep.model.Road;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Allows an administrator to view roads, update their status/hazard
 * level, add new roads, and delete existing ones.
 */
public class RoadManagementPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Source", "Destination", "Distance (km)", "Status", "Hazard"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final RoadDAO roadDAO = new RoadDAO();

    public RoadManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadRoads();
    }

    private JPanel buildTopPanel() {
        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete Selected Road");

        refreshBtn.addActionListener(e -> loadRoads());
        deleteBtn.addActionListener(e -> deleteSelectedRoad());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refreshBtn);
        top.add(deleteBtn);
        return top;
    }

    private JPanel buildBottomPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Road"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField sourceField = new JTextField(10);
        JTextField destField = new JTextField(10);
        JTextField distanceField = new JTextField(6);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"CLEAR", "CONGESTED", "BLOCKED"});
        JComboBox<String> hazardBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        JTextField roadIdField = new JTextField(5);

        int col = 0;
        gbc.gridx = col++; gbc.gridy = 0; formPanel.add(new JLabel("Source:"), gbc);
        gbc.gridx = col++; formPanel.add(sourceField, gbc);
        gbc.gridx = col++; formPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = col++; formPanel.add(destField, gbc);
        gbc.gridx = col++; formPanel.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = col++; formPanel.add(distanceField, gbc);

        col = 0;
        gbc.gridx = col++; gbc.gridy = 1; formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = col++; formPanel.add(statusBox, gbc);
        gbc.gridx = col++; formPanel.add(new JLabel("Hazard:"), gbc);
        gbc.gridx = col++; formPanel.add(hazardBox, gbc);

        JButton addBtn = new JButton("Add New Road");
        gbc.gridx = col++; formPanel.add(addBtn, gbc);

        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update Status of Existing Road (by ID)"));
        updatePanel.add(new JLabel("Road ID:"));
        updatePanel.add(roadIdField);
        updatePanel.add(new JLabel("New Status:"));
        JComboBox<String> updateStatusBox = new JComboBox<>(new String[]{"CLEAR", "CONGESTED", "BLOCKED"});
        updatePanel.add(updateStatusBox);
        updatePanel.add(new JLabel("New Hazard:"));
        JComboBox<String> updateHazardBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        updatePanel.add(updateHazardBox);
        JButton updateBtn = new JButton("Update Road");
        updatePanel.add(updateBtn);

        addBtn.addActionListener(e -> {
            try {
                String source = sourceField.getText().trim();
                String dest = destField.getText().trim();
                double distance = Double.parseDouble(distanceField.getText().trim());
                Road road = new Road(0, source, dest, distance,
                        (String) statusBox.getSelectedItem(), (String) hazardBox.getSelectedItem());
                roadDAO.addRoad(road);
                loadRoads();
                sourceField.setText("");
                destField.setText("");
                distanceField.setText("");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Distance must be a valid number.",
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            } catch (SEEPException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                int roadId = Integer.parseInt(roadIdField.getText().trim());
                roadDAO.updateRoadStatus(roadId, (String) updateStatusBox.getSelectedItem(),
                        (String) updateHazardBox.getSelectedItem());
                loadRoads();
                roadIdField.setText("");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Road ID must be a valid number.",
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            } catch (SEEPException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(formPanel);
        south.add(updatePanel);
        return south;
    }

    private void deleteSelectedRoad() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a road to delete.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int roadId = (int) model.getValueAt(row, 0);
        try {
            roadDAO.deleteRoad(roadId);
            loadRoads();
        } catch (SEEPException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRoads() {
        try {
            model.setRowCount(0);
            List<Road> roads = roadDAO.getAllRoads();
            for (Road r : roads) {
                model.addRow(new Object[]{
                        r.getRoadId(), r.getSource(), r.getDestination(),
                        r.getDistanceKm(), r.getStatus(), r.getHazardLevel()
                });
            }
        } catch (SEEPException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
