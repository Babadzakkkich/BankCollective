import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class Account {
    private int id;
    private String accountNumber;
    private String ownerName;
    private double balance;
    private String currencyCode; // Добавляем код валюты
    private double annualInterestRate;
    private Date dateCreated;
    private List<String> transactionHistory;
    private static int nextId = 1;

    public Account() {
        this.id = nextId++;
        this.accountNumber = "";
        this.ownerName = "";
        this.balance = 0;
        this.currencyCode = "RUB"; // По умолчанию рубли
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
        this.transactionHistory = new ArrayList<>();
    }

    public Account(int id, double balance) {
        this.id = id;
        this.accountNumber = String.valueOf(id);
        this.ownerName = "Client " + id;
        this.balance = balance;
        this.currencyCode = "RUB";
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with initial balance: " + balance + " " + currencyCode);
    }

    public Account(String accountNumber, String ownerName, double initialBalance) {
        this(accountNumber, ownerName, initialBalance, "RUB");
    }

    // Новый конструктор с поддержкой валют
    public Account(String accountNumber, String ownerName, double initialBalance, String currencyCode) {
        this.id = nextId++;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.currencyCode = currencyCode.toUpperCase();
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with initial balance: " + initialBalance + " " + currencyCode);
    }

    // Добавляем геттер и сеттер для валюты
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode.toUpperCase();
        addTransaction("Currency changed to: " + currencyCode);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public double getMonthlyInterest() {
        double monthlyRate = annualInterestRate / 100 / 12;
        return balance * monthlyRate;
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Withdraw: " + amount + " " + currencyCode);
        } else {
            String message = "Failed withdraw attempt: " + amount + " " + currencyCode + ". Insufficient funds or invalid amount.";
            System.out.println(message);
            addTransaction(message);
        }
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposit: " + amount + " " + currencyCode);
        } else {
            String message = "Failed deposit attempt: " + amount + " " + currencyCode + ". Amount must be positive.";
            System.out.println(message);
            addTransaction(message);
        }
    }

    private void addTransaction(String transaction) {
        String timestamp = new Date().toString();
        transactionHistory.add(timestamp + " - " + transaction);
    }

    public String getAccountInfo() {
        return String.format("Account: %s, Owner: %s, Balance: %.2f %s, Created: %s",
                accountNumber, ownerName, balance, currencyCode, dateCreated);
    }

    public String getBalanceInCurrency(String targetCurrency, CbrCurrencyService currencyService) {
        try {
            BigDecimal convertedAmount = currencyService.convert(
                    BigDecimal.valueOf(balance), currencyCode, targetCurrency);
            return String.format("%.2f %s", convertedAmount, targetCurrency);
        } catch (Exception e) {
            return "Ошибка конвертации: " + e.getMessage();
        }
    }

    public double getBalanceInCurrencyValue(String targetCurrency, CbrCurrencyService currencyService) {
        try {
            BigDecimal convertedAmount = currencyService.convert(
                    BigDecimal.valueOf(balance), currencyCode, targetCurrency);
            return convertedAmount.doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка конвертации", e);
        }
    }
}