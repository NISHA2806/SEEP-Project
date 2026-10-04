package com.seep.gui;

import com.seep.dao.ShelterDAO;
import com.seep.exception.SEEPException;
import com.seep.model.Shelter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Allows an administrator to add shelters, update occupancy, and delete
 * shelters.
 */
public class ShelterManagementPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Location", "Capacity", "Occupied", "Available"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final ShelterDAO shelterDAO = new ShelterDAO();

    public ShelterManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadShelters();
    }

    private JPanel buildTopPanel() {
        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete Selected Shelter");

        refreshBtn.addActionListener(e -> loadShelters());
        deleteBtn.addActionListener(e -> deleteSelected());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refreshBtn);
        top.add(deleteBtn);
        return top;
    }

    private JPanel buildBottomPanel() {
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Shelter"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(12);
        JTextField locationField = new JTextField(10);
        JTextField capacityField = new JTextField(6);

        int col = 0;
        gbc.gridx = col++; gbc.gridy = 0; addPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = col++; addPanel.add(nameField, gbc);
        gbc.gridx = col++; addPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = col++; addPanel.add(locationField, gbc);
        gbc.gridx = col++; addPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = col++; addPanel.add(capacityField, gbc);

        JButton addBtn = new JButton("Add Shelter");
        gbc.gridx = col++; addPanel.add(addBtn, gbc);

        JPanel occupancyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        occupancyPanel.setBorder(BorderFactory.createTitledBorder("Update Occupancy (by Shelter ID)"));
        JTextField idField = new JTextField(5);
        JTextField occupiedField = new JTextField(5);
        JButton updateBtn = new JButton("Update Occupancy");
        occupancyPanel.add(new JLabel("Shelter ID:"));
        occupancyPanel.add(idField);
        occupancyPanel.add(new JLabel("New Occupied Count:"));
        occupancyPanel.add(occupiedField);
        occupancyPanel.add(updateBtn);

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String location = locationField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                if (name.isEmpty() || location.isEmpty()) {
                    throw new NumberFormatException(); // reuse validation dialog path below
                }
                Shelter shelter = new Shelter(0, name, location, capacity, 0);
                shelterDAO.addShelter(shelter);
                loadShelters();
                nameField.setText("");
                locationField.setText("");
                capacityField.setText("");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please provide a valid name, location, and numeric capacity.",
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            } catch (SEEPException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                int occupied = Integer.parseInt(occupiedField.getText().trim());
                shelterDAO.updateOccupancy(id, occupied);
                loadShelters();
                idField.setText("");
                occupiedField.setText("");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Shelter ID and occupied count must be numbers.",
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            } catch (SEEPException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(addPanel);
        south.add(occupancyPanel);
        return south;
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shelter to delete.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int shelterId = (int) model.getValueAt(row, 0);
        try {
            shelterDAO.deleteShelter(shelterId);
            loadShelters();
        } catch (SEEPException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadShelters() {
        try {
            model.setRowCount(0);
            List<Shelter> shelters = shelterDAO.getAllShelters();
            for (Shelter s : shelters) {
                model.addRow(new Object[]{
                        s.getShelterId(), s.getName(), s.getLocation(),
                        s.getCapacity(), s.getOccupied(), s.getAvailableSpace()
                });
            }
        } catch (SEEPException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
