import java.util.EnumMap;
import java.util.Map;

/**
 * Кав'ярня. Зберігає фабрики напоїв (Factory Method),
 * продає напої та накопичує загальний прибуток за період.
 */
public class CoffeeShop {

    private final Map<BeverageType, BeverageFactory> factories = new EnumMap<>(BeverageType.class);
    private double totalRevenue = 0;   // загальна виручка
    private double totalCost = 0;      // загальна собівартість
    private int sold = 0;              // кількість проданих напоїв

    public CoffeeShop() {
        factories.put(BeverageType.ESPRESSO,      new EspressoFactory());
        factories.put(BeverageType.AMERICANO,     new AmericanoFactory());
        factories.put(BeverageType.CAPPUCCINO,    new CappuccinoFactory());
        factories.put(BeverageType.LATTE,         new LatteFactory());
        factories.put(BeverageType.HOT_CHOCOLATE, new HotChocolateFactory());
    }

    /** Продати один напій заданого типу. */
    public Beverage sell(BeverageType type) {
        BeverageFactory factory = factories.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Немає такого напою: " + type);
        }
        Beverage beverage = factory.orderBeverage();
        totalRevenue += beverage.getSalePrice();
        totalCost    += beverage.getCost();
        sold++;
        return beverage;
    }

    public double getTotalRevenue() { return totalRevenue; }
    public double getTotalCost()    { return totalCost; }
    public double getTotalProfit()  { return totalRevenue - totalCost; }
    public int getSold()            { return sold; }

    public void printReport() {
        System.out.println("\n========== Звіт кав'ярні ==========");
        System.out.printf("Продано напоїв:        %d%n", sold);
        System.out.printf("Загальна виручка:      %.2f грн%n", totalRevenue);
        System.out.printf("Загальна собівартість: %.2f грн%n", totalCost);
        System.out.printf("ПРИБУТОК:              %.2f грн%n", getTotalProfit());
    }
}
