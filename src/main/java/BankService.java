import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class BankService {
    private Account[] accounts;
    private Map<String, Account> accountMap;
    private CbrCurrencyService currencyService;

    public BankService() {
        accounts = new Account[10];
        for (int i = 0; i < 10; i++) {
            accounts[i] = new Account(i, 10000);
        }

        accountMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            accountMap.put(String.valueOf(i), accounts[i]);
        }

        // Инициализируем сервис валют
        currencyService = new CbrCurrencyService();
        currencyService.loadExchangeRates();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        int id;

        while (true) {
            System.out.print("Введите ID: ");
            id = scanner.nextInt();

            if (id < 0 || id >= accounts.length) {
                System.out.println("Некорректный ID. Попробуйте снова.");
                continue;
            }

            showMainMenu(id, scanner);
        }
    }

    private void showMainMenu(int id, Scanner scanner) {
        int choice;
        do {
            System.out.println("\nОсновное меню");
            System.out.println("1: проверить баланс счета");
            System.out.println("2: снять со счета");
            System.out.println("3: положить на счет");
            System.out.println("4: показать баланс в других валютах");
            System.out.println("5: выйти");
            System.out.print("Введите пункт меню: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.printf("Баланс равен %.2f %s\n",
                            accounts[id].getBalance(), accounts[id].getCurrencyCode());
                    break;
                case 2:
                    System.out.print("Введите сумму для снятия со счета: ");
                    double withdrawAmount = scanner.nextDouble();
                    accounts[id].withdraw(withdrawAmount);
                    break;
                case 3:
                    System.out.print("Введите сумму для пополнения счета: ");
                    double depositAmount = scanner.nextDouble();
                    accounts[id].deposit(depositAmount);
                    break;
                case 4:
                    showBalanceInOtherCurrencies(accounts[id]);
                    break;
                case 5:
                    System.out.println("Выход из меню.");
                    break;
                default:
                    System.out.println("Неверный пункт меню.");
            }
        } while (choice != 5);
    }

    private void showBalanceInOtherCurrencies(Account account) {
        System.out.println("\nБаланс в других валютах:");
        String[] popularCurrencies = {"USD", "EUR", "GBP", "CNY"};
        for (String currency : popularCurrencies) {
            if (!currency.equals(account.getCurrencyCode())) {
                String converted = account.getBalanceInCurrency(currency, currencyService);
                System.out.println("  " + converted);
            }
        }
    }

    public Account createAccount(String accountNumber, String ownerName, double initialBalance) {
        return createAccount(accountNumber, ownerName, initialBalance, "RUB");
    }

    public Account createAccount(String accountNumber, String ownerName, double initialBalance, String currencyCode) {
        if (accountMap.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account with number " + accountNumber + " already exists");
        }

        Account account = new Account(accountNumber, ownerName, initialBalance, currencyCode);
        accountMap.put(accountNumber, account);
        return account;
    }

    public Account getAccount(String accountNumber) {
        Account account = accountMap.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        if (fromAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds for transfer from account: " + fromAccountNumber);
        }

        // Если валюты разные, конвертируем сумму
        if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
            double convertedAmount = currencyService.convert(
                    java.math.BigDecimal.valueOf(amount),
                    fromAccount.getCurrencyCode(),
                    toAccount.getCurrencyCode()
            ).doubleValue();

            fromAccount.withdraw(amount);
            toAccount.deposit(convertedAmount);

            fromAccount.getTransactionHistory().add(
                    String.format("Transfer to %s: %s %.2f (converted to %.2f %s)",
                            toAccountNumber, fromAccount.getCurrencyCode(), amount,
                            convertedAmount, toAccount.getCurrencyCode()));
            toAccount.getTransactionHistory().add(
                    String.format("Transfer from %s: %s %.2f (converted from %.2f %s)",
                            fromAccountNumber, toAccount.getCurrencyCode(), convertedAmount,
                            amount, fromAccount.getCurrencyCode()));
        } else {
            // Если валюты одинаковые, обычный перевод
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);

            fromAccount.getTransactionHistory().add("Transfer to " + toAccountNumber + ": " + amount + " " + fromAccount.getCurrencyCode());
            toAccount.getTransactionHistory().add("Transfer from " + fromAccountNumber + ": " + amount + " " + toAccount.getCurrencyCode());
        }
    }

    public double getTotalBankBalance() {
        double total = 0;
        for (Account account : accountMap.values()) {
            total += account.getBalance();
        }
        return total;
    }

    public double getTotalBankBalanceInCurrency(String currencyCode) {
        double total = 0;
        for (Account account : accountMap.values()) {
            String converted = account.getBalanceInCurrency(currencyCode, currencyService);
            // Извлекаем число из строки "123.45 USD"
            String[] parts = converted.split(" ");
            if (parts.length > 0) {
                try {
                    total += Double.parseDouble(parts[0]);
                } catch (NumberFormatException e) {
                    // Игнорируем ошибки парсинга
                }
            }
        }
        return total;
    }

    public int getAccountsCount() {
        return accountMap.size();
    }

    public CbrCurrencyService getCurrencyService() {
        return currencyService;
    }

    public Map<String, Account> getAllAccounts() {
        return new HashMap<>(accountMap);
    }

    public static void main(String[] args) {
        BankService bankService = new BankService();
        bankService.start();
    }
}