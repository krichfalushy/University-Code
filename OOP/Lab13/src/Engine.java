/** Легковаговик (Flyweight) — двигун. Незмінний спільний об'єкт. */
public class Engine {
    public enum Fuel { Petrol, Diesel, Electric }

    final int power;
    final Fuel fuel;

    // package-private: створюється лише через EngineFactory
    Engine(int power, Fuel fuel) {
        this.power = power;
        this.fuel = fuel;
    }

    @Override
    public String toString() {
        return "Engine{power=" + power + ", fuel=" + fuel + "}";
    }
}
