// ExchangeRate.java
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeRate {
    private Currency currency;
    private BigDecimal rate;
    private int nominal;
    private LocalDate date;

    public ExchangeRate(Currency currency, BigDecimal rate, int nominal, LocalDate date) {
        this.currency = currency;
        this.rate = rate;
        this.nominal = nominal;
        this.date = date;
    }

    // Getters
    public Currency getCurrency() { return currency; }
    public BigDecimal getRate() { return rate; }
    public int getNominal() { return nominal; }
    public LocalDate getDate() { return date; }

    public BigDecimal getRateForOneUnit() {
        return rate.divide(BigDecimal.valueOf(nominal), 6, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public String toString() {
        return String.format("%s: %.4f руб. (за %d ед.)",
                currency.getCharCode(), rate, nominal);
    }
}