import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {
    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService();
    }

    @Test
    void testCreateAccount() {
        Account account = bankService.createAccount("111", "Alice", 1000.0);
        assertNotNull(account);
        assertEquals("111", account.getAccountNumber());
        assertEquals("Alice", account.getOwnerName());
        assertEquals(1000.0, account.getBalance(), 0.001);
    }

    @Test
    void testCreateDuplicateAccount() {
        bankService.createAccount("111", "Alice", 1000.0);
        assertThrows(IllegalArgumentException.class,
                () -> bankService.createAccount("111", "Bob", 500.0));
    }

    @Test
    void testGetAccount() {
        bankService.createAccount("111", "Alice", 1000.0);
        Account account = bankService.getAccount("111");
        assertNotNull(account);
        assertEquals("Alice", account.getOwnerName());
        assertEquals(1000.0, account.getBalance(), 0.001);
    }

    @Test
    void testGetNonExistentAccount() {
        assertThrows(IllegalArgumentException.class,
                () -> bankService.getAccount("999"));
    }

    @Test
    void testTransferBetweenAccounts() {
        bankService.createAccount("111", "Alice", 1000.0);
        bankService.createAccount("222", "Bob", 500.0);

        bankService.transfer("111", "222", 300.0);

        assertEquals(700.0, bankService.getAccount("111").getBalance(), 0.001);
        assertEquals(800.0, bankService.getAccount("222").getBalance(), 0.001);
    }

    @Test
    void testTransferWithInsufficientFunds() {
        bankService.createAccount("111", "Alice", 100.0);
        bankService.createAccount("222", "Bob", 500.0);

        assertThrows(InsufficientFundsException.class,
                () -> bankService.transfer("111", "222", 300.0));
    }

    @Test
    void testTransferToNonExistentAccount() {
        bankService.createAccount("111", "Alice", 1000.0);

        assertThrows(IllegalArgumentException.class,
                () -> bankService.transfer("111", "999", 300.0));
    }

    @Test
    void testTransferFromNonExistentAccount() {
        bankService.createAccount("222", "Bob", 500.0);

        assertThrows(IllegalArgumentException.class,
                () -> bankService.transfer("999", "222", 300.0));
    }

    @Test
    void testGetTotalBankBalance() {
        // Учитываем начальные 10 счетов по 10000 каждый
        double initialBalance = bankService.getTotalBankBalance();

        bankService.createAccount("111", "Alice", 1000.0);
        bankService.createAccount("222", "Bob", 500.0);
        bankService.createAccount("333", "Charlie", 750.0);

        double totalBalance = bankService.getTotalBankBalance();
        assertEquals(initialBalance + 2250.0, totalBalance, 0.001);
    }

    @Test
    void testGetAccountsCount() {
        int initialCount = bankService.getAccountsCount();
        bankService.createAccount("111", "Alice", 1000.0);
        assertEquals(initialCount + 1, bankService.getAccountsCount());
        bankService.createAccount("222", "Bob", 500.0);
        assertEquals(initialCount + 2, bankService.getAccountsCount());
    }

    @Test
    void testMultipleTransfers() {
        bankService.createAccount("111", "Alice", 1000.0);
        bankService.createAccount("222", "Bob", 500.0);
        bankService.createAccount("333", "Charlie", 750.0);

        bankService.transfer("111", "222", 200.0);
        bankService.transfer("222", "333", 100.0);
        bankService.transfer("333", "111", 50.0);

        assertEquals(850.0, bankService.getAccount("111").getBalance(), 0.001);
        assertEquals(600.0, bankService.getAccount("222").getBalance(), 0.001);
        assertEquals(800.0, bankService.getAccount("333").getBalance(), 0.001);
    }

    @Test
    void testAccountIsolation() {
        Account account1 = bankService.createAccount("111", "Alice", 1000.0);
        Account account2 = bankService.createAccount("222", "Bob", 500.0);

        account1.deposit(100.0);
        assertEquals(1100.0, account1.getBalance(), 0.001);
        assertEquals(500.0, account2.getBalance(), 0.001);

        account2.withdraw(200.0);
        assertEquals(1100.0, account1.getBalance(), 0.001);
        assertEquals(300.0, account2.getBalance(), 0.001);
    }
}