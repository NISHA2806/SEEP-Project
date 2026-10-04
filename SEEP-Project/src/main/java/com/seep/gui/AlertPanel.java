package com.seep.gui;

import com.seep.dao.AlertDAO;
import com.seep.exception.SEEPException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Displays emergency alerts. When constructed with allowIssue = true
 * (used by the Admin dashboard) it also shows a form for issuing new
 * alerts.
 */
public class AlertPanel extends JPanel {

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> alertList = new JList<>(listModel);
    private final AlertDAO alertDAO = new AlertDAO();

    public AlertPanel(boolean allowIssue) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        alertList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(alertList), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Alerts");
        refreshBtn.addActionListener(e -> loadAlerts());

        if (allowIssue) {
            JPanel issuePanel = new JPanel(new BorderLayout(5, 5));
            JTextField messageField = new JTextField();
            JComboBox<String> severityBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
            JButton issueBtn = new JButton("Issue Alert");

            JPanel controls = new JPanel(new BorderLayout(5, 5));
            controls.add(messageField, BorderLayout.CENTER);
            controls.add(severityBox, BorderLayout.EAST);

            issuePanel.add(new JLabel("New Alert Message:"), BorderLayout.NORTH);
            issuePanel.add(controls, BorderLayout.CENTER);
            issuePanel.add(issueBtn, BorderLayout.SOUTH);

            issueBtn.addActionListener(e -> {
                String message = messageField.getText().trim();
                if (message.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Alert message cannot be empty.",
                            "Missing information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    alertDAO.addAlert(message, (String) severityBox.getSelectedItem());
                    messageField.setText("");
                    loadAlerts();
                } catch (SEEPException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            add(issuePanel, BorderLayout.SOUTH);
            add(refreshBtn, BorderLayout.NORTH);
        } else {
            add(refreshBtn, BorderLayout.NORTH);
        }

        loadAlerts();
    }

    private void loadAlerts() {
        try {
            listModel.clear();
            List<String> alerts = alertDAO.getAllAlerts();
            if (alerts.isEmpty()) {
                listModel.addElement("No active alerts.");
            } else {
                for (String a : alerts) {
                    listModel.addElement(a);
                }
            }
        } catch (SEEPException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
