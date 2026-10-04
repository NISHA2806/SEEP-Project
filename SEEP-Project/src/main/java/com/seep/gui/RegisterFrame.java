package com.seep.gui;

import com.seep.dao.UserDAO;
import com.seep.exception.SEEPException;

import javax.swing.*;
import java.awt.*;

/**
 * Registration screen for new citizen users.
 */
public class RegisterFrame extends JFrame {

    private final JTextField fullNameField = new JTextField(18);
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JPasswordField confirmField = new JPasswordField(18);
    private final UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("SEEP - Register New Account");
        setSize(400, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; panel.add(fullNameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(usernameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; panel.add(confirmField, gbc);

        JButton registerBtn = new JButton("Create Account");
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);

        add(panel);

        registerBtn.addActionListener(e -> handleRegister());
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.",
                    "Validation error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userDAO.register(username, password, fullName);
            JOptionPane.showMessageDialog(this, "Account created successfully. You can now log in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (SEEPException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
