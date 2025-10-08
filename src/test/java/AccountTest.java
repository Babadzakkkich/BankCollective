import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class AccountTest {

    @Test
    void testDefaultConstructor() {
        Account account = new Account();

        assertEquals(0, account.getId());
        assertEquals(0, account.getBalance());
        assertEquals(0, account.getAnnualInterestRate());
        assertNotNull(account.getDateCreated());
        assertTrue(account.getDateCreated() instanceof Date);
    }

    @Test
    void testParameterizedConstructor() {
        Account account = new Account(1155, 300000);

        assertEquals(1155, account.getId());
        assertEquals(300000, account.getBalance());
        assertEquals(0, account.getAnnualInterestRate());
        assertNotNull(account.getDateCreated());
    }

    @Test
    void testSettersAndGetters() {
        Account account = new Account();

        account.setId(1234);
        account.setBalance(50000);
        account.setAnnualInterestRate(5.5);

        assertEquals(1234, account.getId());
        assertEquals(50000, account.getBalance());
        assertEquals(5.5, account.getAnnualInterestRate());
    }

    @Test
    void testDeposit() {
        Account account = new Account(1, 10000);

        account.deposit(5000);
        assertEquals(15000, account.getBalance());

        // Попытка внести отрицательную сумму
        account.deposit(-1000);
        assertEquals(15000, account.getBalance()); // Баланс не должен измениться
    }

    @Test
    void testWithdraw() {
        Account account = new Account(1, 10000);

        account.withdraw(3000);
        assertEquals(7000, account.getBalance());

        // Попытка снять больше, чем есть на счете
        account.withdraw(8000);
        assertEquals(7000, account.getBalance()); // Баланс не должен измениться

        // Попытка снять отрицательную сумму
        account.withdraw(-1000);
        assertEquals(7000, account.getBalance()); // Баланс не должен измениться
    }

    @Test
    void testGetMonthlyInterest() {
        Account account = new Account(1155, 300000);
        account.setAnnualInterestRate(6.5);

        // Расчет: 300000 * (6.5 / 12 / 100) = 300000 * 0.00541667 ≈ 1625.00
        double expectedInterest = 300000 * (6.5 / 12 / 100);
        assertEquals(expectedInterest, account.getMonthlyInterest(), 0.01);
    }

    @Test
    void testCompleteScenario() {
        // Тестирование полного сценария из задания
        Account account = new Account(1155, 300000);
        account.setAnnualInterestRate(6.5);

        // Снятие 16500
        account.withdraw(16500);
        assertEquals(283500, account.getBalance());

        // Пополнение 50000
        account.deposit(50000);
        assertEquals(333500, account.getBalance());

        // Проверка ежемесячного процента
        double monthlyInterest = account.getMonthlyInterest();
        double expectedInterest = 333500 * (6.5 / 12 / 100);
        assertEquals(expectedInterest, monthlyInterest, 0.01);

        // Проверка даты создания
        assertNotNull(account.getDateCreated());
        assertTrue(account.getDateCreated().getTime() <= System.currentTimeMillis());
    }
}