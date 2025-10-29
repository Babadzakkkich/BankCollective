// Currency.java
import java.util.Objects;

public class Currency {
    private String code;
    private String name;
    private String charCode;

    public Currency(String code, String name, String charCode) {
        this.code = code;
        this.name = name;
        this.charCode = charCode;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCharCode() { return charCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, charCode);
    }
}