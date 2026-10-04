package com.seep.gui;

import com.seep.model.User;
import com.seep.util.AppLogger;

import javax.swing.*;
import java.awt.*;

/**
 * Main window shown to citizen (non-admin) users after login.
 */
public class UserDashboard extends JFrame {

    public UserDashboard(User user) {
        setTitle("SEEP - Citizen Dashboard | Welcome, " + user.getFullName());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Find Evacuation Route", new RouteFinderPanel());
        tabs.addTab("Shelters", new ShelterPanel());
        tabs.addTab("Alerts", new AlertPanel(false));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel welcome = new JLabel("Logged in as: " + user.getFullName() + " (Citizen)");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 14));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            AppLogger.info("User logged out: " + user.getUsername());
            dispose();
            new LoginFrame().setVisible(true);
        });
        header.add(welcome, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
