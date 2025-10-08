import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private int id;
    private String accountNumber;
    private String ownerName;
    private double balance;
    private double annualInterestRate;
    private Date dateCreated;
    private List<String> transactionHistory;
    private static int nextId = 1;

    public Account() {
        this.id = nextId++;
        this.accountNumber = "";
        this.ownerName = "";
        this.balance = 0;
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
        this.transactionHistory = new ArrayList<>();
    }

    public Account(int id, double balance) {
        this.id = id;
        this.accountNumber = String.valueOf(id);
        this.ownerName = "Client " + id;
        this.balance = balance;
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with initial balance: $" + balance);
    }

    public Account(String accountNumber, String ownerName, double initialBalance) {
        this.id = nextId++; // Автоматически генерируем ID
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with initial balance: $" + initialBalance);
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
            addTransaction("Withdraw: $" + amount);
        } else {
            String message = "Failed withdraw attempt: $" + amount + ". Insufficient funds or invalid amount.";
            System.out.println(message);
            addTransaction(message);
        }
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposit: $" + amount);
        } else {
            String message = "Failed deposit attempt: $" + amount + ". Amount must be positive.";
            System.out.println(message);
            addTransaction(message);
        }
    }

    private void addTransaction(String transaction) {
        String timestamp = new Date().toString();
        transactionHistory.add(timestamp + " - " + transaction);
    }

    public String getAccountInfo() {
        return String.format("Account: %s, Owner: %s, Balance: $%.2f, Created: %s",
                accountNumber, ownerName, balance, dateCreated);
    }
}