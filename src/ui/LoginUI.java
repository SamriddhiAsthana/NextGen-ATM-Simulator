package ui;

import model.Account;
import services.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {

    private JTextField accField;
    private JPasswordField pinField;
    private AuthService authService = new AuthService();
    private JButton loginBtn;

    public LoginUI() {
        setTitle("Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(173, 216, 230));

        JLabel accLabel = new JLabel("Account No:");
        accLabel.setBounds(50, 50, 100, 30);

        accField = new JTextField();
        accField.setBounds(150, 50, 150, 30);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(50, 100, 100, 30);

        pinField = new JPasswordField();
        pinField.setBounds(150, 100, 150, 30);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 160, 150, 35);

        loginBtn.setBackground(new Color(0, 102, 204));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);

        loginBtn.addActionListener(e -> handleLogin());

        panel.add(accLabel);
        panel.add(accField);
        panel.add(pinLabel);
        panel.add(pinField);
        panel.add(loginBtn);

        add(panel);
    }

    private void handleLogin() {
        String accNo = accField.getText().trim();
        String pin = new String(pinField.getPassword()).trim();

        // ✅ Input validation
        if (accNo.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Account Number and PIN",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Account acc = authService.login(accNo, pin);

            dispose();
            SwingUtilities.invokeLater(() -> new DashboardUI(acc).setVisible(true));

        } catch (RuntimeException ex) {

            // 🔒 If account locked → disable button temporarily
            if (ex.getMessage().toLowerCase().contains("locked")) {
                loginBtn.setEnabled(false);

                // Re-enable after 2 minutes
                new Timer(120000, ev -> loginBtn.setEnabled(true)).start();
            }

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}