/** Абстрактний продукт «Автобус». */
public abstract class Bus {
    private final int cost;       // вартість одиниці транспорту
    private final int usageCost;  // вартість експлуатації, грн/км

    protected Bus(int cost, int usageCost) {
        this.cost = cost;
        this.usageCost = usageCost;
    }

    public int getCost() { return cost; }
    public int getUsageCost() { return usageCost; }

    public void goByWay() {
        System.out.println("Bus runs!");
    }
}
