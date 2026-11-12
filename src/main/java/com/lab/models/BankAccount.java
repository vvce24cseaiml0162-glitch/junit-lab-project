package com.lab.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BankAccount {
    private final String accountNumber;
    private final String accountHolderName;
    private double balance;
    private boolean active;
    private final List<Transaction> transactions;

    public BankAccount(String accountNumber, String accountHolderName, double initialDeposit) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty");
        }
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.active = true;
        this.transactions = new ArrayList<>();
        if (initialDeposit > 0) {
            this.transactions.add(new Transaction(
                    "TXN-" + System.currentTimeMillis() + "-0",
                    Transaction.Type.DEPOSIT,
                    initialDeposit,
                    "Initial deposit"
            ));
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void credit(double amount, String description) {
        this.balance += amount;
        this.transactions.add(new Transaction(
                "TXN-" + System.currentTimeMillis() + "-" + (transactions.size() + 1),
                Transaction.Type.DEPOSIT,
                amount,
                description
        ));
    }

    public void debit(double amount, String description) {
        this.balance -= amount;
        this.transactions.add(new Transaction(
                "TXN-" + System.currentTimeMillis() + "-" + (transactions.size() + 1),
                Transaction.Type.WITHDRAWAL,
                amount,
                description
        ));
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber='" + accountNumber + '\'' +
                ", holder='" + accountHolderName + '\'' +
                ", balance=" + balance +
                ", active=" + active +
                ", transactionCount=" + transactions.size() +
                '}';
    }
}
