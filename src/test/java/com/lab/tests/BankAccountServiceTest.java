package com.lab.tests;

import com.lab.exceptions.AccountLockedException;
import com.lab.exceptions.InsufficientFundsException;
import com.lab.exceptions.InvalidAmountException;
import com.lab.models.BankAccount;
import com.lab.services.BankAccountService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bank Account Service Transaction Test Suite")
@Tag("unit")
public class BankAccountServiceTest {

    private BankAccountService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankAccountService();
    }

    @AfterEach
    void tearDown() {
        bankService.clearAll();
    }

    @Nested
    @DisplayName("Account Creation Tests")
    class AccountCreationTests {

        @Test
        @DisplayName("Should successfully create a new bank account with valid initial deposit")
        void testCreateAccountSuccess() {
            BankAccount account = bankService.createAccount("Pruthvik V", 5000.0);

            assertAll("Account Creation Verification",
                    () -> assertNotNull(account.getAccountNumber()),
                    () -> assertTrue(account.getAccountNumber().startsWith("SB")),
                    () -> assertEquals("Pruthvik V", account.getAccountHolderName()),
                    () -> assertEquals(5000.0, account.getBalance()),
                    () -> assertTrue(account.isActive()),
                    () -> assertEquals(1, account.getTransactions().size())
            );
        }

        @Test
        @DisplayName("Should reject account creation when deposit is below minimum INR 500")
        void testCreateAccountBelowMinimumDeposit() {
            InvalidAmountException ex = assertThrows(InvalidAmountException.class, () ->
                    bankService.createAccount("Test User", 200.0)
            );
            assertTrue(ex.getMessage().contains("Minimum initial deposit"));
        }

        @Test
        @DisplayName("Should reject invalid or empty account holder name")
        void testCreateAccountInvalidName() {
            assertThrows(IllegalArgumentException.class, () ->
                    bankService.createAccount("", 1000.0)
            );
        }
    }

    @Nested
    @DisplayName("Deposit & Credit Operations Tests")
    class DepositTests {

        @Test
        @DisplayName("Should credit valid deposit amount and update account balance")
        void testDepositSuccess() {
            BankAccount account = bankService.createAccount("Deposit User", 1000.0);
            double newBalance = bankService.deposit(account.getAccountNumber(), 2500.0);

            assertEquals(3500.0, newBalance);
            assertEquals(3500.0, account.getBalance());
            assertEquals(2, account.getTransactions().size());
        }

        @ParameterizedTest(name = "Non-positive deposit amount: {0}")
        @ValueSource(doubles = {0.0, -100.0, -0.01})
        @DisplayName("Should throw InvalidAmountException on zero or negative deposit amounts")
        void testInvalidDepositAmount(double amount) {
            BankAccount account = bankService.createAccount("User A", 1000.0);
            assertThrows(InvalidAmountException.class, () ->
                    bankService.deposit(account.getAccountNumber(), amount)
            );
        }

        @Test
        @DisplayName("Should reject deposit when account is locked")
        void testDepositOnLockedAccount() {
            BankAccount account = bankService.createAccount("Locked User", 1000.0);
            bankService.lockAccount(account.getAccountNumber());

            assertThrows(AccountLockedException.class, () ->
                    bankService.deposit(account.getAccountNumber(), 500.0)
            );
        }
    }

    @Nested
    @DisplayName("Withdrawal & Debit Operations Tests")
    class WithdrawalTests {

        @Test
        @DisplayName("Should successfully withdraw amount within available balance")
        void testWithdrawSuccess() {
            BankAccount account = bankService.createAccount("Withdraw User", 10000.0);
            double remaining = bankService.withdraw(account.getAccountNumber(), 3500.0);

            assertEquals(6500.0, remaining);
            assertEquals(6500.0, account.getBalance());
        }

        @Test
        @DisplayName("Should throw InsufficientFundsException when withdrawal exceeds balance")
        void testWithdrawInsufficientBalance() {
            BankAccount account = bankService.createAccount("Low Balance User", 2000.0);

            InsufficientFundsException ex = assertThrows(InsufficientFundsException.class, () ->
                    bankService.withdraw(account.getAccountNumber(), 2500.0)
            );
            assertTrue(ex.getMessage().contains("Insufficient balance"));
        }

        @Test
        @DisplayName("Should throw InvalidAmountException when withdrawal exceeds daily limit of INR 50,000")
        void testWithdrawExceedingDailyLimit() {
            BankAccount account = bankService.createAccount("Rich User", 100000.0);

            InvalidAmountException ex = assertThrows(InvalidAmountException.class, () ->
                    bankService.withdraw(account.getAccountNumber(), 60000.0)
            );
            assertTrue(ex.getMessage().contains("exceeds daily limit"));
        }

        @Test
        @DisplayName("Should reject withdrawal when account is locked")
        void testWithdrawOnLockedAccount() {
            BankAccount account = bankService.createAccount("Locked Withdraw", 5000.0);
            bankService.lockAccount(account.getAccountNumber());

            assertThrows(AccountLockedException.class, () ->
                    bankService.withdraw(account.getAccountNumber(), 1000.0)
            );
        }
    }

    @Nested
    @DisplayName("Inter-Account Fund Transfer Tests")
    class TransferTests {

        @Test
        @DisplayName("Should transfer funds between two active accounts atomically")
        void testTransferSuccess() {
            BankAccount sender = bankService.createAccount("Sender", 15000.0);
            BankAccount receiver = bankService.createAccount("Receiver", 5000.0);

            boolean result = bankService.transfer(sender.getAccountNumber(), receiver.getAccountNumber(), 4000.0);

            assertTrue(result);
            assertEquals(11000.0, sender.getBalance());
            assertEquals(9000.0, receiver.getBalance());
        }

        @Test
        @DisplayName("Should reject fund transfer to the same account")
        void testTransferToSameAccount() {
            BankAccount account = bankService.createAccount("Self Transfer", 10000.0);

            assertThrows(IllegalArgumentException.class, () ->
                    bankService.transfer(account.getAccountNumber(), account.getAccountNumber(), 1000.0)
            );
        }

        @Test
        @DisplayName("Should reject fund transfer when sender has insufficient funds")
        void testTransferInsufficientFunds() {
            BankAccount sender = bankService.createAccount("Sender Poor", 1000.0);
            BankAccount receiver = bankService.createAccount("Receiver Rich", 5000.0);

            assertThrows(InsufficientFundsException.class, () ->
                    bankService.transfer(sender.getAccountNumber(), receiver.getAccountNumber(), 2000.0)
            );
            // Verify sender balance remained intact
            assertEquals(1000.0, sender.getBalance());
        }
    }
}
