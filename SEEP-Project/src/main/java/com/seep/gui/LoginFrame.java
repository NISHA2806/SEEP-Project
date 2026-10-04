package com.seep.gui;

import com.seep.dao.UserDAO;
import com.seep.exception.SEEPException;
import com.seep.model.Admin;
import com.seep.model.User;
import com.seep.util.AppLogger;

import javax.swing.*;
import java.awt.*;

/**
 * Entry-point login screen. Authenticates against the users table and
 * routes to either the AdminDashboard or the UserDashboard depending on
 * the role of the returned User (Polymorphism in action).
 */
public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("SEEP - Smart Emergency Evacuation Planner | Login");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Smart Emergency Evacuation Planner", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register as Citizen");

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        gbc.gridy = 4;
        panel.add(registerBtn, gbc);

        JLabel hint = new JLabel("<html><center>Default admin login:<br>username = admin, password = admin123</center></html>");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(Color.GRAY);
        gbc.gridy = 5;
        panel.add(hint, gbc);

        add(panel);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
        });

        // allow pressing Enter in the password field to submit
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userDAO.login(username, password);
            AppLogger.info("User logged in: " + user.getUsername() + " (" + user.getRole() + ")");

            this.dispose();
            if (user instanceof Admin) {
                new AdminDashboard((Admin) user).setVisible(true);
            } else {
                new UserDashboard(user).setVisible(true);
            }
        } catch (SEEPException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
