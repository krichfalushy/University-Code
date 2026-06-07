import java.util.List;

/**
 * Кав'ярня, що працює на машині конкретного виробника (через абстрактну фабрику).
 * Рахує чистий прибуток за N днів з урахуванням:
 *   + прибутку від продажу напоїв,
 *   - вартості кавової машини (одноразово),
 *   - щоденного обслуговування.
 */
public class CoffeeShop {
    private final CoffeeMachineFactory factory;
    private final CoffeeMachine machine;
    private final List<Beverage> menu;

    public CoffeeShop(CoffeeMachineFactory factory) {
        this.factory = factory;
        this.machine = factory.createMachine();
        this.menu = List.of(
                factory.createEspresso(),
                factory.createCappuccino(),
                factory.createLatte());
    }

    /**
     * Чистий прибуток за days днів.
     * dailySales — скільки порцій кожного напою (за позицією меню) продається на день.
     */
    public double netProfit(int days, int[] dailySales) {
        double dailyBeverageProfit = 0;
        for (int i = 0; i < menu.size(); i++) {
            dailyBeverageProfit += menu.get(i).getProfit() * dailySales[i];
        }
        double grossProfit = dailyBeverageProfit * days;                 // прибуток з напоїв
        double maintenance = machine.getDailyMaintenance() * days;       // обслуговування
        return grossProfit - maintenance - machine.getMachineCost();     // мінус машина
    }

    /** Перший день, коли кав'ярня виходить у плюс (повертає -1, якщо не виходить). */
    public int breakEvenDay(int maxDays, int[] dailySales) {
        for (int d = 1; d <= maxDays; d++) {
            if (netProfit(d, dailySales) >= 0) return d;
        }
        return -1;
    }

    public String getManufacturer() { return factory.getManufacturer(); }
    public CoffeeMachine getMachine() { return machine; }
}
