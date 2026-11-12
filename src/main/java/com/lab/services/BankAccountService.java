package com.lab.services;

import com.lab.exceptions.AccountLockedException;
import com.lab.exceptions.InsufficientFundsException;
import com.lab.exceptions.InvalidAmountException;
import com.lab.models.BankAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BankAccountService {

    private final Map<String, BankAccount> accounts = new HashMap<>();
    private final AtomicLong accountCounter = new AtomicLong(1000000000L);
    public static final double DAILY_WITHDRAWAL_LIMIT = 50000.0;
    public static final double MINIMUM_INITIAL_DEPOSIT = 500.0;

    public synchronized BankAccount createAccount(String holderName, double initialDeposit) {
        if (holderName == null || holderName.trim().length() < 2) {
            throw new IllegalArgumentException("Account holder name must be at least 2 characters");
        }
        if (initialDeposit < MINIMUM_INITIAL_DEPOSIT) {
            throw new InvalidAmountException("Minimum initial deposit of INR " + MINIMUM_INITIAL_DEPOSIT + " required");
        }

        String accountNumber = "SB" + accountCounter.incrementAndGet();
        BankAccount account = new BankAccount(accountNumber, holderName.trim(), initialDeposit);
        accounts.put(accountNumber, account);
        return account;
    }

    public BankAccount getAccount(String accountNumber) {
        if (accountNumber == null || !accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account " + accountNumber + " does not exist");
        }
        return accounts.get(accountNumber);
    }

    public synchronized double deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        BankAccount account = getAccount(accountNumber);
        if (!account.isActive()) {
            throw new AccountLockedException("Cannot deposit to locked account: " + accountNumber);
        }
        account.credit(amount, "Deposit");
        return account.getBalance();
    }

    public synchronized double withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        if (amount > DAILY_WITHDRAWAL_LIMIT) {
            throw new InvalidAmountException("Withdrawal amount exceeds daily limit of INR " + DAILY_WITHDRAWAL_LIMIT);
        }
        BankAccount account = getAccount(accountNumber);
        if (!account.isActive()) {
            throw new AccountLockedException("Cannot withdraw from locked account: " + accountNumber);
        }
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + account.getBalance() + ", Requested: " + amount);
        }
        account.debit(amount, "Withdrawal");
        return account.getBalance();
    }

    public synchronized boolean transfer(String fromAccountNum, String toAccountNum, double amount) {
        if (fromAccountNum == null || toAccountNum == null || fromAccountNum.equals(toAccountNum)) {
            throw new IllegalArgumentException("Source and destination accounts must be distinct");
        }
        withdraw(fromAccountNum, amount);
        deposit(toAccountNum, amount);
        return true;
    }

    public void lockAccount(String accountNumber) {
        BankAccount account = getAccount(accountNumber);
        account.setActive(false);
    }

    public void unlockAccount(String accountNumber) {
        BankAccount account = getAccount(accountNumber);
        account.setActive(true);
    }

    public void clearAll() {
        accounts.clear();
    }
}
