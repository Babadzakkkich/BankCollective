import java.util.Scanner;
import java.math.BigDecimal;

public class Main {
    private static BankService bankService = new BankService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Банковская система с поддержкой валют ===");

        initializeSampleData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Выберите опцию: ");

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> depositMoney();
                case 3 -> withdrawMoney();
                case 4 -> transferMoney();
                case 5 -> checkBalance();
                case 6 -> showTransactionHistory();
                case 7 -> showBankSummary();
                case 8 -> showExchangeRates();
                case 9 -> convertCurrency();
                case 10 -> updateExchangeRates();
                case 11 -> showAllAccounts();
                case 0 -> {
                    running = false;
                    System.out.println("Выход из системы...");
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Создать новый счёт");
        System.out.println("2. Пополнить счёт");
        System.out.println("3. Снять деньги");
        System.out.println("4. Перевести деньги");
        System.out.println("5. Проверить баланс");
        System.out.println("6. История операций");
        System.out.println("7. Общая информация банка");
        System.out.println("8. Курсы валют");
        System.out.println("9. Конвертировать валюту");
        System.out.println("10. Обновить курсы валют");
        System.out.println("11. Показать все счета");
        System.out.println("0. Выход");
    }

    private static void initializeSampleData() {
        try {
            bankService.createAccount("1001", "Иван Иванов", 5000.0, "RUB");
            bankService.createAccount("1002", "Мария Петрова", 3000.0, "RUB");
            bankService.createAccount("1003", "Алексей Сидоров", 10000.0, "RUB");
            bankService.createAccount("1004", "Джон Смит", 1000.0, "USD");
            bankService.createAccount("1005", "Анна Мюллер", 800.0, "EUR");
            System.out.println("Демо-данные загружены!");
        } catch (Exception e) {
            System.out.println("Ошибка при создании демо-данных: " + e.getMessage());
        }
    }

    private static void createAccount() {
        System.out.println("\n=== Создание нового счёта ===");
        String accountNumber = getStringInput("Введите номер счёта: ");
        String ownerName = getStringInput("Введите имя владельца: ");
        double initialBalance = getDoubleInput("Введите начальный баланс: ");

        System.out.println("Доступные валюты:");
        bankService.getCurrencyService().getAvailableCurrencies().forEach(currency ->
                System.out.println("  " + currency.getCharCode() + " - " + currency.getName()));

        String currencyCode = getStringInput("Введите код валюты (RUB, USD, EUR и т.д.): ");

        try {
            Account account = bankService.createAccount(accountNumber, ownerName, initialBalance, currencyCode);
            System.out.println("Счёт успешно создан!");
            System.out.println(account.getAccountInfo());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void depositMoney() {
        System.out.println("\n=== Пополнение счёта ===");
        String accountNumber = getStringInput("Введите номер счёта: ");
        double amount = getDoubleInput("Введите сумму для пополнения: ");

        try {
            Account account = bankService.getAccount(accountNumber);
            account.deposit(amount);
            System.out.printf("Успешно пополнено: %.2f %s%n", amount, account.getCurrencyCode());
            System.out.printf("Новый баланс: %.2f %s%n", account.getBalance(), account.getCurrencyCode());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void withdrawMoney() {
        System.out.println("\n=== Снятие денег ===");
        String accountNumber = getStringInput("Введите номер счёта: ");
        double amount = getDoubleInput("Введите сумму для снятия: ");

        try {
            Account account = bankService.getAccount(accountNumber);
            account.withdraw(amount);
            System.out.printf("Успешно снято: %.2f %s%n", amount, account.getCurrencyCode());
            System.out.printf("Новый баланс: %.2f %s%n", account.getBalance(), account.getCurrencyCode());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void transferMoney() {
        System.out.println("\n=== Перевод денег ===");
        String fromAccount = getStringInput("Введите номер счёта отправителя: ");
        String toAccount = getStringInput("Введите номер счёта получателя: ");
        double amount = getDoubleInput("Введите сумму перевода: ");

        try {
            Account fromAcc = bankService.getAccount(fromAccount);
            Account toAcc = bankService.getAccount(toAccount);

            System.out.printf("Перевод из %s в %s: %.2f %s%n",
                    fromAccount, toAccount, amount, fromAcc.getCurrencyCode());

            if (!fromAcc.getCurrencyCode().equals(toAcc.getCurrencyCode())) {
                double convertedAmount = bankService.getCurrencyService().convert(
                        BigDecimal.valueOf(amount),
                        fromAcc.getCurrencyCode(),
                        toAcc.getCurrencyCode()
                ).doubleValue();
                System.out.printf("Сумма будет конвертирована в: %.2f %s%n",
                        convertedAmount, toAcc.getCurrencyCode());
            }

            bankService.transfer(fromAccount, toAccount, amount);
            System.out.println("Перевод выполнен успешно!");

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void checkBalance() {
        System.out.println("\n=== Проверка баланса ===");
        String accountNumber = getStringInput("Введите номер счёта: ");

        try {
            Account account = bankService.getAccount(accountNumber);
            System.out.println(account.getAccountInfo());

            // Показываем баланс в других популярных валютах
            System.out.println("\nБаланс в других валютах:");
            String[] popularCurrencies = {"USD", "EUR", "GBP", "CNY"};
            for (String currency : popularCurrencies) {
                if (!currency.equals(account.getCurrencyCode())) {
                    String converted = account.getBalanceInCurrency(currency, bankService.getCurrencyService());
                    System.out.println("  " + converted);
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void showTransactionHistory() {
        System.out.println("\n=== История операций ===");
        String accountNumber = getStringInput("Введите номер счёта: ");

        try {
            Account account = bankService.getAccount(accountNumber);
            System.out.println("История операций для счёта " + accountNumber + ":");
            if (account.getTransactionHistory().isEmpty()) {
                System.out.println("  История операций пуста");
            } else {
                for (String transaction : account.getTransactionHistory()) {
                    System.out.println("  • " + transaction);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void showBankSummary() {
        System.out.println("\n=== Общая информация банка ===");
        System.out.printf("Общее количество счетов: %d%n", bankService.getAccountsCount());
        System.out.printf("Общий баланс банка в RUB: %.2f%n", bankService.getTotalBankBalance());
        System.out.printf("Общий баланс банка в USD: %.2f%n", bankService.getTotalBankBalanceInCurrency("USD"));
        System.out.printf("Общий баланс банка в EUR: %.2f%n", bankService.getTotalBankBalanceInCurrency("EUR"));
    }

    private static void showExchangeRates() {
        System.out.println("\n=== Текущие курсы валют ЦБ РФ ===");
        try {
            var rates = bankService.getCurrencyService().getAllExchangeRates();
            rates.forEach(rate -> {
                if (!rate.getCurrency().getCharCode().equals("RUB")) {
                    System.out.printf("  %s: %.4f руб. за %d %s%n",
                            rate.getCurrency().getCharCode(),
                            rate.getRate(),
                            rate.getNominal(),
                            rate.getCurrency().getCharCode());
                }
            });
        } catch (Exception e) {
            System.out.println("Ошибка при получении курсов валют: " + e.getMessage());
        }
    }

    private static void convertCurrency() {
        System.out.println("\n=== Конвертация валют ===");

        showExchangeRates();

        String fromCurrency = getStringInput("Из валюты (RUB, USD, EUR и т.д.): ").toUpperCase();
        String toCurrency = getStringInput("В валюту (RUB, USD, EUR и т.д.): ").toUpperCase();
        double amount = getDoubleInput("Сумма для конвертации: ");

        try {
            BigDecimal result = bankService.getCurrencyService().convert(
                    BigDecimal.valueOf(amount), fromCurrency, toCurrency);

            System.out.printf("\nРезультат конвертации:%n");
            System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);

        } catch (Exception e) {
            System.out.println("Ошибка конвертации: " + e.getMessage());
        }
    }

    private static void updateExchangeRates() {
        System.out.println("\n=== Обновление курсов валют ===");
        try {
            bankService.getCurrencyService().loadExchangeRates();
            System.out.println("Курсы валют успешно обновлены!");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении курсов: " + e.getMessage());
        }
    }

    private static void showAllAccounts() {
        System.out.println("\n=== Все счета банка ===");
        var accounts = bankService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("Счетов нет");
        } else {
            accounts.values().forEach(account ->
                    System.out.println("  " + account.getAccountInfo()));
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите целое число!");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число!");
            }
        }
    }
}