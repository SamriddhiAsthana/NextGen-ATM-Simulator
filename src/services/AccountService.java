package services;

import model.Account;
import util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class AccountService {

    private FileHandler fileHandler = new FileHandler();

    public double getBalance(String accNo) {
        String[] data = fileHandler.getAccount(accNo);
        return Double.parseDouble(data[2]);
    }

    private boolean validatePin(String accNo, String pin) {
        String[] data = fileHandler.getAccount(accNo);
        return data != null && data[1].equals(pin);
    }

    public boolean deposit(String accNo, double amount, String pin) {
        if (!validatePin(accNo, pin)) return false;

        double updated = getBalance(accNo) + amount;

        fileHandler.updateBalance(accNo, updated);
        fileHandler.saveTransaction(accNo, "DEPOSIT", amount);

        return true;
    }

    public boolean withdraw(String accNo, double amount, String pin) {
        if (!validatePin(accNo, pin)) return false;

        double current = getBalance(accNo);
        if (amount > current) return false;

        fileHandler.updateBalance(accNo, current - amount);
        fileHandler.saveTransaction(accNo, "WITHDRAW", amount);

        return true;
    }

    public boolean transfer(String from, String to, double amount, String pin) {
        if (!validatePin(from, pin)) return false;

        double senderBal = getBalance(from);
        if (amount > senderBal) return false;

        String[] receiver = fileHandler.getAccount(to);
        if (receiver == null) return false;

        double receiverBal = Double.parseDouble(receiver[2]);

        fileHandler.updateBalance(from, senderBal - amount);
        fileHandler.updateBalance(to, receiverBal + amount);

        fileHandler.saveTransaction(from, "TRANSFER_SENT", amount);
        fileHandler.saveTransaction(to, "TRANSFER_RECEIVED", amount);

        return true;
    }

    public String getTransactionHistory(String accNo) {
        return fileHandler.getTransactions(accNo);
    }

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();

        for (String line : fileHandler.getAllLines()) {
            String[] data = line.split(",");
            if (data.length >= 4) {
                list.add(new Account(
                        data[0],
                        data[1],
                        Double.parseDouble(data[2]),
                        data[3]
                ));
            }
        }

        return list;
    }

    public boolean changePin(String accNo, String oldPin, String newPin) {
        if (!validatePin(accNo, oldPin)) return false;
        fileHandler.updatePin(accNo, newPin);
        return true;
    }

    public void removeLastWithdrawEntry(String accNo) {
        fileHandler.removeLastWithdrawEntry(accNo);
    }

    // ✅ NEW
    public void removeLastDepositEntry(String accNo) {
        fileHandler.removeLastDepositEntry(accNo);
    }

    // ✅ NEW
    public void addCustomEntry(String accNo, String type, double amount) {
        fileHandler.saveTransaction(accNo, type, amount);
    }
}