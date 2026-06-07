/** Абстрактний продукт «Тролейбус». */
public abstract class Trolleybus {
    private final int cost;
    private final int usageCost;

    protected Trolleybus(int cost, int usageCost) {
        this.cost = cost;
        this.usageCost = usageCost;
    }

    public int getCost() { return cost; }
    public int getUsageCost() { return usageCost; }

    public void goByContactNetwork() {
        System.out.println("Trolleybus runs");
    }
}
