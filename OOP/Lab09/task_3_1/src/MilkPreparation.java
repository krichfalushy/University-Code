/** Молочний варіант: додаємо молоко (дорожче). */
public class MilkPreparation implements Preparation {
    private final int milkVolume;

    public MilkPreparation(int milkVolume) {
        this.milkVolume = milkVolume;
    }

    @Override public void addLiquid() {
        System.out.printf("Put some milk: %s ml...%n", milkVolume);
    }
    @Override public int extraCost() { return (int) (milkVolume / 20.0); }
    @Override public String description() { return "with milk"; }
}
