package model;

import java.util.Date;

public class Transaction {
    private String accountNumber;
    private String type;
    private double amount;
    private Date date;

    public Transaction(String accNo, String type, double amount) {
        this.accountNumber = accNo;
        this.type = type;
        this.amount = amount;
        this.date = new Date();
    }

    @Override
    public String toString() {
        return accountNumber + " | " + type + " | " + amount + " | " + date;
    }
}