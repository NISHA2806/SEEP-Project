package com.seep.gui;

import com.seep.model.Admin;
import com.seep.util.AppLogger;

import javax.swing.*;
import java.awt.*;

/**
 * Main window shown to administrators after login. Gives full access to
 * shelter management, road/hazard management, alert broadcasting, and
 * the same route finder tool available to citizens (useful for testing
 * routes before an emergency).
 */
public class AdminDashboard extends JFrame {

    public AdminDashboard(Admin admin) {
        setTitle("SEEP - Administrator Dashboard | " + admin.getFullName());
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Shelters", new ShelterManagementPanel());
        tabs.addTab("Manage Roads & Hazards", new RoadManagementPanel());
        tabs.addTab("Issue / View Alerts", new AlertPanel(true));
        tabs.addTab("Route Finder (Test)", new RouteFinderPanel());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel welcome = new JLabel("Logged in as: " + admin.getFullName() + " (" + admin.getDesignation() + ")");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 14));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            AppLogger.info("Admin logged out: " + admin.getUsername());
            dispose();
            new LoginFrame().setVisible(true);
        });
        header.add(welcome, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
