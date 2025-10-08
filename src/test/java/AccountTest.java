import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private Account account;
    private static final String ACCOUNT_NUMBER = "123456789";
    private static final String OWNER_NAME = "John Doe";
    private static final double INITIAL_BALANCE = 1000.0;

    @BeforeEach
    void setUp() {
        account = new Account(ACCOUNT_NUMBER, OWNER_NAME, INITIAL_BALANCE);
    }

    @Test
    void testAccountCreation() {
        assertNotNull(account);
        assertEquals(ACCOUNT_NUMBER, account.getAccountNumber());
        assertEquals(OWNER_NAME, account.getOwnerName());
        assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001);
    }

    @Test
    void testAccountCreationWithIdAndBalance() {
        Account accountWithId = new Account(5, 2000.0);
        assertEquals("5", accountWithId.getAccountNumber());
        assertEquals("Client 5", accountWithId.getOwnerName());
        assertEquals(2000.0, accountWithId.getBalance(), 0.001);
    }

    @Test
    void testDeposit() {
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance(), 0.001);
    }

    @Test
    void testDepositNegativeAmount() {
        account.deposit(-100);
        assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001);
    }

    @Test
    void testDepositZeroAmount() {
        account.deposit(0);
        assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001);
    }

    @Test
    void testWithdraw() {
        account.withdraw(300.0);
        assertEquals(700.0, account.getBalance(), 0.001);
    }

    @Test
    void testWithdrawInsufficientFunds() {
        account.withdraw(2000.0);
        assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001);
    }

    @Test
    void testWithdrawNegativeAmount() {
        account.withdraw(-100);
        assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001);
    }

    @Test
    void testWithdrawZeroAmount() {
        account.withdraw(0);
        assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001);
    }

    @Test
    void testTransactionHistory() {
        account.deposit(200.0);
        account.withdraw(100.0);

        List<String> history = account.getTransactionHistory();
        assertEquals(3, history.size());
        assertTrue(history.get(1).contains("Deposit: $200.0"));
        assertTrue(history.get(2).contains("Withdraw: $100.0"));
    }

    @Test
    void testGetAccountInfo() {
        String info = account.getAccountInfo();
        assertTrue(info.contains(ACCOUNT_NUMBER));
        assertTrue(info.contains(OWNER_NAME));
        assertFalse(info.contains("1000.00"));
    }

    @Test
    void testGetDateCreated() {
        assertNotNull(account.getDateCreated());
    }

    @Test
    void testMonthlyInterest() {
        account.setAnnualInterestRate(12);
        double monthlyInterest = account.getMonthlyInterest();
        assertEquals(10.0, monthlyInterest, 0.001);
    }

    @Test
    void testSetters() {
        account.setId(999);
        account.setAccountNumber("999");
        account.setOwnerName("New Owner");
        account.setBalance(5000.0);
        account.setAnnualInterestRate(5.0);

        assertEquals(999, account.getId());
        assertEquals("999", account.getAccountNumber());
        assertEquals("New Owner", account.getOwnerName());
        assertEquals(5000.0, account.getBalance(), 0.001);
        assertEquals(5.0, account.getAnnualInterestRate(), 0.001);
    }
}