package model;

public class Account {

    private String accNo;
    private String pin;
    private double balance;
    private String name;

    public Account(String accNo, String pin, double balance, String name) {
        this.accNo = accNo;
        this.pin = pin;
        this.balance = balance;
        this.name = name;
    }

    public String getAccNo() {
        return accNo;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}