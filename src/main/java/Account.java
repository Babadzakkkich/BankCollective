import java.util.Date;

public class Account {
    private int id;
    private double balance;
    private double annualInterestRate;
    private Date dateCreated;

    // Безаргументный конструктор
    public Account() {
        this.id = 0;
        this.balance = 0;
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
    }

    // Конструктор с указанными id и balance
    public Account(int id, double balance) {
        this.id = id;
        this.balance = balance;
        this.annualInterestRate = 0;
        this.dateCreated = new Date();
    }

    // Getter и setter методы
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    // Метод для получения ежемесячного процента
    public double getMonthlyInterest() {
        double monthlyInterestRate = annualInterestRate / 12 / 100;
        return balance * monthlyInterestRate;
    }

    // Метод для снятия денег
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }

    // Метод для пополнения счета
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
}