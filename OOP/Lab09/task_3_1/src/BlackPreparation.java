/** Чорний варіант: додаємо гарячу воду. */
public class BlackPreparation implements Preparation {
    private final int waterVolume;

    public BlackPreparation(int waterVolume) {
        this.waterVolume = waterVolume;
    }

    @Override public void addLiquid() {
        System.out.printf("Put some hot water: %s ml...%n", waterVolume);
    }
    @Override public int extraCost() { return 0; }
    @Override public String description() { return "black"; }
}
