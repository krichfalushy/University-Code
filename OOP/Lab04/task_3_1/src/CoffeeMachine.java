/**
 * Кавова машина: має вартість купівлі та щоденну вартість обслуговування.
 */
public abstract class CoffeeMachine {
    private final double machineCost;        // вартість самої машини
    private final double dailyMaintenance;   // обслуговування на день

    protected CoffeeMachine(double machineCost, double dailyMaintenance) {
        this.machineCost = machineCost;
        this.dailyMaintenance = dailyMaintenance;
    }

    public abstract String getManufacturer();
    public double getMachineCost()      { return machineCost; }
    public double getDailyMaintenance() { return dailyMaintenance; }
}
