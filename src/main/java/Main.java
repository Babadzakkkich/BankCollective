import java.util.Scanner;

public class Main {
    private static BankService bankService = new BankService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Банковская система ===");

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
        System.out.println("0. Выход");
    }

    private static void initializeSampleData() {
        try {
            bankService.createAccount("1001", "Иван Иванов", 5000.0);
            bankService.createAccount("1002", "Мария Петрова", 3000.0);
            bankService.createAccount("1003", "Алексей Сидоров", 10000.0);
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

        try {
            Account account = bankService.createAccount(accountNumber, ownerName, initialBalance);
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
            System.out.printf("Успешно пополнено: $%.2f%n", amount);
            System.out.printf("Новый баланс: $%.2f%n", account.getBalance());
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
            System.out.printf("Успешно снято: $%.2f%n", amount);
            System.out.printf("Новый баланс: $%.2f%n", account.getBalance());
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
            bankService.transfer(fromAccount, toAccount, amount);
            System.out.printf("Успешно переведено: $%.2f%n", amount);
            System.out.printf("Со счёта %s на счёт %s%n", fromAccount, toAccount);
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
            for (String transaction : account.getTransactionHistory()) {
                System.out.println("  • " + transaction);
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void showBankSummary() {
        System.out.println("\n=== Общая информация банка ===");
        System.out.printf("Общий баланс банка: $%.2f%n", bankService.getTotalBankBalance());
        System.out.printf("Количество счетов: %d%n", bankService.getAccountsCount());
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