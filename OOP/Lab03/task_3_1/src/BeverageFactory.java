/**
 * Творець (Creator) патерну Фабричний метод.
 * createBeverage() — фабричний метод; orderBeverage() — спільна бізнес-логіка.
 */
public abstract class BeverageFactory {

    /** Фабричний метод — створює конкретний напій. */
    public abstract Beverage createBeverage();

    /** Бізнес-операція над продуктом. */
    public Beverage orderBeverage() {
        Beverage beverage = createBeverage();
        System.out.println("Готуємо: " + beverage.getName());
        return beverage;
    }
}
