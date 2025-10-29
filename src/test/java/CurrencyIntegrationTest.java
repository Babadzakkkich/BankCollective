import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyIntegrationTest {
    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService();
    }

    @Test
    void testMultiCurrencyAccountOperations() {
        // Создаем счета в разных валютах
        Account rubAccount = bankService.createAccount("RUB001", "Рублевый счет", 10000.0, "RUB");
        Account usdAccount = bankService.createAccount("USD001", "Долларовый счет", 1000.0, "USD");
        Account eurAccount = bankService.createAccount("EUR001", "Евро счет", 800.0, "EUR");

        // Проверяем создание счетов
        assertEquals("RUB", rubAccount.getCurrencyCode());
        assertEquals("USD", usdAccount.getCurrencyCode());
        assertEquals("EUR", eurAccount.getCurrencyCode());

        // Проверяем операции в соответствующих валютах
        rubAccount.deposit(5000.0);
        assertEquals(15000.0, rubAccount.getBalance(), 0.001);

        usdAccount.withdraw(200.0);
        assertEquals(800.0, usdAccount.getBalance(), 0.001);

        // Проверяем историю операций с указанием валюты
        var rubHistory = rubAccount.getTransactionHistory();
        assertTrue(rubHistory.get(rubHistory.size() - 1).contains("RUB"));

        var usdHistory = usdAccount.getTransactionHistory();
        assertTrue(usdHistory.get(usdHistory.size() - 1).contains("USD"));
    }

    @Test
    void testCurrencyConversionInTransfers() {
        // Создаем счета в разных валютах
        bankService.createAccount("FROM", "Отправитель", 10000.0, "RUB");
        bankService.createAccount("TO", "Получатель", 100.0, "USD");

        // Перевод между разными валютами должен работать (с конвертацией)
        assertDoesNotThrow(() -> {
            bankService.transfer("FROM", "TO", 5000.0);
        });

        // Проверяем, что балансы изменились
        Account fromAccount = bankService.getAccount("FROM");
        Account toAccount = bankService.getAccount("TO");

        assertTrue(fromAccount.getBalance() < 10000.0);
        assertTrue(toAccount.getBalance() > 100.0);
    }

    @Test
    void testBankSummaryWithMultipleCurrencies() {
        // Создаем счета в разных валютах
        bankService.createAccount("R1", "Рубль 1", 5000.0, "RUB");
        bankService.createAccount("U1", "Доллар 1", 500.0, "USD");
        bankService.createAccount("E1", "Евро 1", 400.0, "EUR");

        // Проверяем, что методы summary работают
        assertDoesNotThrow(() -> {
            double totalRub = bankService.getTotalBankBalance();
            double totalUsd = bankService.getTotalBankBalanceInCurrency("USD");
            double totalEur = bankService.getTotalBankBalanceInCurrency("EUR");

            // Баланс в рублях должен быть положительным (учитываем начальные 10 счетов по 10000)
            assertTrue(totalRub >= 5000.0 + 500.0 + 400.0 + 100000.0); // 100000 - это 10 счетов по 10000

            // Балансы в других валютах также должны быть положительными
            assertTrue(totalUsd > 0);
            assertTrue(totalEur > 0);

            // Логируем для отладки
            System.out.printf("Total RUB: %.2f%n", totalRub);
            System.out.printf("Total USD: %.2f%n", totalUsd);
            System.out.printf("Total EUR: %.2f%n", totalEur);
        });
    }
}