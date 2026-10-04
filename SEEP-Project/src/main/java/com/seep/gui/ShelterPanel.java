package com.seep.gui;

import com.seep.dao.ShelterDAO;
import com.seep.exception.SEEPException;
import com.seep.model.Shelter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Read-only view of all shelters, their capacity and current occupancy.
 * Used inside both the User and Admin dashboards.
 */
public class ShelterPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Location", "Capacity", "Occupied", "Available"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final ShelterDAO shelterDAO = new ShelterDAO();

    public ShelterPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton refreshBtn = new JButton("Refresh Shelter List");
        refreshBtn.addActionListener(e -> loadShelters());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadShelters();
    }

    public void loadShelters() {
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
