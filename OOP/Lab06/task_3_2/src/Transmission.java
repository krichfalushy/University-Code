/** Трансмісія (додано за описом умови). Прототип: clone(). */
public class Transmission implements Cloneable {
    public enum Type { Manual, Automatic }

    private Type type;
    private int gears;

    public Transmission(Type type, int gears) {
        this.type = type;
        this.gears = gears;
    }

    public void setGears(int gears) { this.gears = gears; }

    @Override
    public Transmission clone() {
        try {
            return (Transmission) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String toString() {
        return "Transmission{type=" + type + ", gears=" + gears + "}";
    }
}
