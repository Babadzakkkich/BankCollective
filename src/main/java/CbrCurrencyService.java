// CbrCurrencyService.java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

public class CbrCurrencyService {
    private static final String CBR_API_URL = "https://www.cbr-xml-daily.ru/daily_json.js";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private Map<String, ExchangeRate> exchangeRates;
    private LocalDate lastUpdate;

    public CbrCurrencyService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.exchangeRates = new HashMap<>();
        this.lastUpdate = LocalDate.now().minusDays(1); // Принудительно обновить при первом запросе
    }


    public void loadExchangeRates() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CBR_API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                parseExchangeRates(response.body());
                lastUpdate = LocalDate.now();
                System.out.println("Курсы валют успешно обновлены");
            } else {
                System.out.println("Ошибка при загрузке курсов: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка подключения к ЦБ РФ: " + e.getMessage());
            // Можно использовать запасные значения
            loadFallbackRates();
        }
    }

    private void parseExchangeRates(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode valuteNode = root.path("Valute");

            // Добавляем российский рубль как базовую валюту
            Currency rubCurrency = new Currency("R00001", "Российский рубль", "RUB");
            ExchangeRate rubRate = new ExchangeRate(rubCurrency, BigDecimal.ONE, 1, LocalDate.now());
            exchangeRates.put("RUB", rubRate);

            // Парсим другие валюты
            valuteNode.fields().forEachRemaining(entry -> {
                JsonNode currencyNode = entry.getValue();
                String id = currencyNode.path("ID").asText();
                String name = currencyNode.path("Name").asText();
                String charCode = currencyNode.path("CharCode").asText();
                int numCode = currencyNode.path("NumCode").asInt();

                BigDecimal value = new BigDecimal(currencyNode.path("Value").asText().replace(",", "."));
                int nominal = currencyNode.path("Nominal").asInt();

                Currency currency = new Currency(id, name, charCode);
                ExchangeRate rate = new ExchangeRate(currency, value, nominal, LocalDate.now());

                exchangeRates.put(charCode, rate);
            });

        } catch (Exception e) {
            System.out.println("Ошибка парсинга данных ЦБ РФ: " + e.getMessage());
            loadFallbackRates();
        }
    }


    private void loadFallbackRates() {
        exchangeRates.clear();

        // Базовая валюта - рубль
        Currency rub = new Currency("R00001", "Российский рубль", "RUB");
        exchangeRates.put("RUB", new ExchangeRate(rub, BigDecimal.ONE, 1, LocalDate.now()));

        // Популярные валюты с примерными курсами
        exchangeRates.put("USD", new ExchangeRate(
                new Currency("R01235", "Доллар США", "USD"),
                new BigDecimal("90.0"), 1, LocalDate.now()));

        exchangeRates.put("EUR", new ExchangeRate(
                new Currency("R01239", "Евро", "EUR"),
                new BigDecimal("98.0"), 1, LocalDate.now()));

        exchangeRates.put("GBP", new ExchangeRate(
                new Currency("R01035", "Фунт стерлингов", "GBP"),
                new BigDecimal("114.0"), 1, LocalDate.now()));

        exchangeRates.put("CNY", new ExchangeRate(
                new Currency("R01375", "Китайский юань", "CNY"),
                new BigDecimal("12.5"), 1, LocalDate.now()));

        System.out.println("Используются запасные курсы валют");
    }


    public ExchangeRate getExchangeRate(String currencyCode) {
        // Обновляем курсы, если они устарели (старше 1 дня)
        if (lastUpdate.isBefore(LocalDate.now())) {
            loadExchangeRates();
        }

        ExchangeRate rate = exchangeRates.get(currencyCode.toUpperCase());
        if (rate == null) {
            throw new IllegalArgumentException("Валюта не найдена: " + currencyCode);
        }
        return rate;
    }


    public List<ExchangeRate> getAllExchangeRates() {
        if (lastUpdate.isBefore(LocalDate.now())) {
            loadExchangeRates();
        }
        return new ArrayList<>(exchangeRates.values());
    }


    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount;
        }

        ExchangeRate fromRate = getExchangeRate(fromCurrency);
        ExchangeRate toRate = getExchangeRate(toCurrency);

        // Конвертируем через рубли
        BigDecimal amountInRub = amount.multiply(fromRate.getRateForOneUnit());
        return amountInRub.divide(toRate.getRateForOneUnit(), 2, BigDecimal.ROUND_HALF_UP);
    }


    public List<Currency> getAvailableCurrencies() {
        return getAllExchangeRates().stream()
                .map(ExchangeRate::getCurrency)
                .toList();
    }
}