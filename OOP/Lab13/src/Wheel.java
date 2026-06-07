/** Легковаговик (Flyweight) — колесо. Незмінний спільний об'єкт. */
public class Wheel {
    public enum Material { Steel, Alloy }

    final private Material material;
    final int diameter;

    // package-private: створюється лише через WheelFactory
    Wheel(int diameter) {
        this.material = Material.Steel;
        this.diameter = diameter;
    }

    @Override
    public String toString() {
        return "Wheel{material=" + material + ", diameter=" + diameter + "}";
    }
}
