package ui;

import model.Account;
import services.AccountService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Account account;
    private AccountService service = new AccountService();

    private boolean showBalance = true;
    private JTextArea historyArea;

    public DashboardUI(Account acc) {
        this.account = acc;

        setTitle("Premium Banking Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(240, 700));
        panel.setBackground(new Color(15, 35, 80));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MyBank");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(20, 10, 20, 10));

        panel.add(title);

        String[] menu = {
                "Dashboard",
                "Transfer",
                "Deposit",
                "Withdraw",
                "History",
                "Generate QR",
                "Change PIN"
        };

        for (String item : menu) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(200, 45));
            btn.setBackground(new Color(15, 35, 80));
            btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());

            btn.addActionListener(e -> cardLayout.show(mainPanel, item));
            panel.add(btn);
        }

        return panel;
    }

    private JPanel createMainPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createTransferPanel(), "Transfer");
        mainPanel.add(createDepositPanel(), "Deposit");
        mainPanel.add(createWithdrawPanel(), "Withdraw");
        mainPanel.add(createHistoryPanel(), "History");
        mainPanel.add(createQRPanel(), "Generate QR");
        mainPanel.add(createChangePinPanel(), "Change PIN");

        return mainPanel;
    }

    private JPanel createQRPanel() {
        JPanel panel = createFormPanel("Generate QR");

        JTextArea qrArea = new JTextArea();
        qrArea.setBounds(150, 120, 300, 200);
        qrArea.setEditable(false);

        JButton generateBtn = createButton("Generate QR", 350);

        generateBtn.addActionListener(e -> {
            String qrData =
                    "ACCOUNT: " + account.getAccNo() +
                    "\nNAME: " + account.getName();

            qrArea.setText(qrData);
        });

        panel.add(qrArea);
        panel.add(generateBtn);

        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = createFormPanel("Transfer Money");

        List<Account> accounts = service.getAllAccounts();

        String[] accList = accounts.stream()
                .filter(a -> !a.getAccNo().equals(account.getAccNo()))
                .map(Account::getAccNo)
                .toArray(String[]::new);

        JComboBox<String> to = new JComboBox<>(accList);

        JTextField amt = new JTextField();
        JPasswordField pin = new JPasswordField();

        addForm(panel, "To Account", to, 100);
        addForm(panel, "Amount", amt, 150);
        addForm(panel, "PIN", pin, 200);

        JButton btn = createButton("Transfer", 250);

        btn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amt.getText());
                String toAcc = (String) to.getSelectedItem();

                boolean success = service.transfer(
                        account.getAccNo(),
                        toAcc,
                        amount,
                        new String(pin.getPassword())
                );

                if (success) {
                    JOptionPane.showMessageDialog(this, "Transfer Successful");
                    refreshHistory();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid PIN / Account / Balance");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error");
            }
        });

        panel.add(btn);
        return panel;
    }

    private JPanel createDepositPanel() {
        JPanel panel = createFormPanel("Deposit");

        JTextField amt = new JTextField();
        JPasswordField pin = new JPasswordField();

        addForm(panel, "Amount", amt, 120);
        addForm(panel, "PIN", pin, 170);

        JButton btn = createButton("Deposit", 230);

        btn.addActionListener(e -> {
            double amount = Double.parseDouble(amt.getText());

            boolean success = service.deposit(
                    account.getAccNo(),
                    amount,
                    new String(pin.getPassword())
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Deposited");
                refreshHistory();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong PIN");
            }
        });

        panel.add(btn);
        return panel;
    }

    private JPanel createWithdrawPanel() {
        JPanel panel = createFormPanel("Withdraw");

        JTextField amt = new JTextField();
        JPasswordField pin = new JPasswordField();

        addForm(panel, "Amount", amt, 120);
        addForm(panel, "PIN", pin, 170);

        JButton btn = createButton("Withdraw", 230);

        JButton collectBtn = new JButton("Collect");
        collectBtn.setBounds(250, 290, 150, 40);
        collectBtn.setEnabled(false);

        JLabel timerLabel = new JLabel("Time Left: 30 sec");
        timerLabel.setBounds(500, 120, 250, 40);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        final double[] withdrawnAmount = {0};
        final boolean[] collected = {false};
        final int[] timeLeft = {30};

        panel.add(collectBtn);
        panel.add(timerLabel);

        collectBtn.addActionListener(e -> {
            collected[0] = true;
            collectBtn.setEnabled(false);
            timerLabel.setText("Collected");

            JOptionPane.showMessageDialog(this,
                    "Cash collected successfully.");
        });

        btn.addActionListener(e -> {
            double amount = Double.parseDouble(amt.getText());

            boolean success = service.withdraw(
                    account.getAccNo(),
                    amount,
                    new String(pin.getPassword())
            );

            if (success) {
                withdrawnAmount[0] = amount;
                collected[0] = false;
                timeLeft[0] = 30;

                collectBtn.setEnabled(true);

                javax.swing.Timer countdown =
                        new javax.swing.Timer(1000, ev -> {
                            timeLeft[0]--;
                            timerLabel.setText(
                                    "Time Left: " + timeLeft[0] + " sec"
                            );
                        });

                countdown.start();

                javax.swing.Timer timer =
                        new javax.swing.Timer(30000, ev -> {
                            countdown.stop();

                            if (!collected[0]) {
                                service.deposit(
                                        account.getAccNo(),
                                        withdrawnAmount[0],
                                        account.getPin()
                                );

                                service.removeLastWithdrawEntry(account.getAccNo());
                                service.removeLastDepositEntry(account.getAccNo());

                                service.addCustomEntry(
                                        account.getAccNo(),
                                        "ROLLED_BACK",
                                        withdrawnAmount[0]
                                );

                                timerLabel.setText("Time Expired");
                                collectBtn.setEnabled(false);

                                refreshHistory();

                                JOptionPane.showMessageDialog(this,
                                        "Amount rolled back.");
                            }
                        });

                timer.setRepeats(false);
                timer.start();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Wrong PIN / Insufficient Balance");
            }
        });

        panel.add(btn);
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(null);

        JLabel nameLabel = new JLabel("Name: " + account.getName());
        nameLabel.setBounds(30, 20, 300, 25);

        JLabel accLabel = new JLabel("Account No: " + account.getAccNo());
        accLabel.setBounds(30, 50, 300, 25);

        JLabel balanceLabel = new JLabel();
        balanceLabel.setBounds(30, 100, 400, 40);

        double bal = service.getBalance(account.getAccNo());
        balanceLabel.setText("Balance: ₹ " + bal);

        JButton eyeBtn = new JButton("👁");
        eyeBtn.setBounds(350, 100, 50, 40);

        eyeBtn.addActionListener(e -> {
            if (showBalance) {
                balanceLabel.setText("Balance: ₹ ******");
            } else {
                balanceLabel.setText(
                        "Balance: ₹ " +
                                service.getBalance(account.getAccNo())
                );
            }

            showBalance = !showBalance;
        });

        panel.add(nameLabel);
        panel.add(accLabel);
        panel.add(balanceLabel);
        panel.add(eyeBtn);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        historyArea = new JTextArea();
        historyArea.setEditable(false);

        refreshHistory();

        panel.add(new JScrollPane(historyArea));

        return panel;
    }

    private void refreshHistory() {
        if (historyArea != null) {
            historyArea.setText(
                    service.getTransactionHistory(account.getAccNo())
            );
        }
    }

    private JPanel createFormPanel(String titleText) {
        JPanel panel = new JPanel(null);

        JLabel title = new JLabel(titleText);
        title.setBounds(100, 30, 300, 30);

        panel.add(title);

        return panel;
    }

    private void addForm(JPanel panel, String label,
                         JComponent field, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(100, y, 100, 30);

        field.setBounds(200, y, 200, 30);

        panel.add(l);
        panel.add(field);
    }

    private JButton createButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(200, y, 200, 40);
        return btn;
    }

    private JPanel createChangePinPanel() {
        JPanel panel = createFormPanel("Change PIN");

        JPasswordField oldPin = new JPasswordField();
        JPasswordField newPin = new JPasswordField();

        addForm(panel, "Old PIN", oldPin, 120);
        addForm(panel, "New PIN", newPin, 170);

        JButton btn = createButton("Update PIN", 240);

        btn.addActionListener(e -> {
            boolean success = service.changePin(
                    account.getAccNo(),
                    new String(oldPin.getPassword()),
                    new String(newPin.getPassword())
            );

            if (success) {
                account = new Account(
                        account.getAccNo(),
                        new String(newPin.getPassword()),
                        account.getBalance(),
                        account.getName()
                );

                JOptionPane.showMessageDialog(this,
                        "PIN Updated Successfully");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Wrong Old PIN");
            }
        });

        panel.add(btn);

        return panel;
    }
}