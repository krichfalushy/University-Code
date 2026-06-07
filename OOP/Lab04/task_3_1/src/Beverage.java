/**
 * Напій, приготований кавовою машиною конкретного виробника.
 * cost — собівартість інгредієнтів; price — ціна продажу клієнту.
 */
public abstract class Beverage {
    private final double cost;
    private final double price;

    protected Beverage(double cost, double price) {
        this.cost = cost;
        this.price = price;
    }

    public abstract String getName();
    public double getCost()   { return cost; }
    public double getPrice()  { return price; }
    public double getProfit() { return price - cost; }
}
