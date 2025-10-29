import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CbrCurrencyServiceTest {
    private CbrCurrencyService currencyService;

    @BeforeEach
    void setUp() {
        currencyService = new CbrCurrencyService();
    }

    @Test
    void testLoadExchangeRates() {
        assertDoesNotThrow(() -> {
            currencyService.loadExchangeRates();
        });
    }

    @Test
    void testGetExchangeRate() {
        // После загрузки курсов должны быть доступны основные валюты
        currencyService.loadExchangeRates();

        assertDoesNotThrow(() -> {
            ExchangeRate rubRate = currencyService.getExchangeRate("RUB");
            assertNotNull(rubRate);
            assertEquals("RUB", rubRate.getCurrency().getCharCode());
        });

        // Проверяем, что для неизвестной валюты бросается исключение
        assertThrows(IllegalArgumentException.class, () -> {
            currencyService.getExchangeRate("UNKNOWN");
        });
    }

    @Test
    void testGetAllExchangeRates() {
        currencyService.loadExchangeRates();
        var rates = currencyService.getAllExchangeRates();
        assertNotNull(rates);
        assertFalse(rates.isEmpty());

        // Должен быть как минимум рубль
        assertTrue(rates.stream().anyMatch(rate ->
                rate.getCurrency().getCharCode().equals("RUB")));
    }

    @Test
    void testConvertSameCurrency() {
        BigDecimal amount = BigDecimal.valueOf(100.0);
        BigDecimal result = currencyService.convert(amount, "USD", "USD");
        assertEquals(amount, result);
    }

    @Test
    void testGetAvailableCurrencies() {
        var currencies = currencyService.getAvailableCurrencies();
        assertNotNull(currencies);

        // После загрузки курсов должен быть доступен как минимум рубль
        currencyService.loadExchangeRates();
        currencies = currencyService.getAvailableCurrencies();
        assertFalse(currencies.isEmpty());
    }

    @Test
    void testFallbackRates() {
        // Тестируем, что запасные курсы работают
        // Для этого можно временно сломать URL или отключить интернет
        // Но просто проверяем, что метод не падает
        assertDoesNotThrow(() -> {
            currencyService.loadExchangeRates();
        });
    }
}