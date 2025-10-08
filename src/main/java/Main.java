import java.util.Scanner;

public class Main {
    private static Account[] accounts = new Account[10];

    public static void main(String[] args) {
        initializeAccounts();
        runBankApplication();
    }

    // Инициализация 10 счетов
    private static void initializeAccounts() {
        for (int i = 0; i < 10; i++) {
            accounts[i] = new Account(i, 10000);
        }
        System.out.println("Банковская система запущена. Создано 10 счетов.");
    }

    // Запуск банковского приложения
    private static void runBankApplication() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Запрос ID счета
            int accountId = getValidAccountId(scanner);

            // Отображение главного меню для выбранного счета
            showMainMenu(scanner, accountId);
        }
    }

    // Получение корректного ID счета
    private static int getValidAccountId(Scanner scanner) {
        int accountId;

        while (true) {
            System.out.print("\nВведите ID счета (0-9): ");
            if (scanner.hasNextInt()) {
                accountId = scanner.nextInt();
                if (accountId >= 0 && accountId < 10) {
                    break;
                } else {
                    System.out.println("Неверный ID. Пожалуйста, введите ID от 0 до 9.");
                }
            } else {
                System.out.println("Неверный формат ID. Пожалуйста, введите число.");
                scanner.next(); // Очистка некорректного ввода
            }
        }

        return accountId;
    }

    // Отображение главного меню
    private static void showMainMenu(Scanner scanner, int accountId) {
        Account currentAccount = accounts[accountId];

        while (true) {
            System.out.println("\n=== Счет ID: " + accountId + " ===");
            System.out.println("Основное меню");
            System.out.println("1: проверить баланс счета");
            System.out.println("2: снять со счета");
            System.out.println("3: положить на счет");
            System.out.println("4: выйти в меню выбора счета");
            System.out.print("Введите пункт меню: ");

            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                System.out.println("Неверный ввод. Пожалуйста, введите число от 1 до 4.");
                scanner.next(); // Очистка некорректного ввода
                continue;
            }

            switch (choice) {
                case 1:
                    checkBalance(currentAccount);
                    break;
                case 2:
                    withdrawMoney(scanner, currentAccount);
                    break;
                case 3:
                    depositMoney(scanner, currentAccount);
                    break;
                case 4:
                    System.out.println("Выход из меню счета ID: " + accountId);
                    return;
                default:
                    System.out.println("Неверный пункт меню. Пожалуйста, выберите от 1 до 4.");
            }
        }
    }

    // Проверка баланса
    private static void checkBalance(Account account) {
        System.out.printf("Текущий баланс: %.2f руб.\n", account.getBalance());
    }

    // Снятие денег
    private static void withdrawMoney(Scanner scanner, Account account) {
        System.out.print("Введите сумму для снятия со счета: ");
        double amount;

        if (scanner.hasNextDouble()) {
            amount = scanner.nextDouble();
            if (amount > 0) {
                double oldBalance = account.getBalance();
                account.withdraw(amount);
                if (account.getBalance() < oldBalance) {
                    System.out.printf("Успешно снято: %.2f руб.\n", amount);
                    System.out.printf("Новый баланс: %.2f руб.\n", account.getBalance());
                } else {
                    System.out.println("Ошибка: Недостаточно средств для снятия");
                }
            } else {
                System.out.println("Ошибка: Сумма для снятия должна быть положительной");
            }
        } else {
            System.out.println("Ошибка: Неверный формат суммы");
            scanner.next(); // Очистка некорректного ввода
        }
    }

    // Пополнение счета
    private static void depositMoney(Scanner scanner, Account account) {
        System.out.print("Введите сумму для пополнения счета: ");
        double amount;

        if (scanner.hasNextDouble()) {
            amount = scanner.nextDouble();
            if (amount > 0) {
                account.deposit(amount);
                System.out.printf("Успешно внесено: %.2f руб.\n", amount);
                System.out.printf("Новый баланс: %.2f руб.\n", account.getBalance());
            } else {
                System.out.println("Ошибка: Сумма для пополнения должна быть положительной");
            }
        } else {
            System.out.println("Ошибка: Неверный формат суммы");
            scanner.next(); // Очистка некорректного ввода
        }
    }
}