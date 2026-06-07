/**
 * Автомобіль. Прототип із ГЛИБОКИМ клонуванням:
 * clone() копіює не лише сам Car, а й кожну складову (Engine/Wheel/Transmission),
 * щоб клон не ділив компоненти з оригіналом.
 */
public class Car implements Cloneable {
    public enum CarColor { White, Black, Red, Grey }
    public enum Type { Sedan, Hatchback, SUV }

    private Type type;
    private CarColor carColor;
    private Engine engine;
    private Wheel wheel;
    private Transmission transmission;

    public Car(Type type, CarColor carColor, Engine engine,
               Wheel wheel, Transmission transmission) {
        this.type = type;
        this.carColor = carColor;
        this.engine = engine;
        this.wheel = wheel;
        this.transmission = transmission;
    }

    public Engine getEngine()             { return engine; }
    public Wheel getWheel()               { return wheel; }
    public Transmission getTransmission() { return transmission; }
    public void setColor(CarColor c)      { this.carColor = c; }

    /**
     * Глибока копія: кожна складова клонується окремо.
     * Без цього clone() дав би shallow-копію зі спільними компонентами.
     */
    @Override
    public Car clone() {
        try {
            Car copy = (Car) super.clone();
            copy.engine = this.engine.clone();
            copy.wheel = this.wheel.clone();
            copy.transmission = this.transmission.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String toString() {
        return "Car:\n" +
                "  type=" + type + "\n" +
                "  carColor=" + carColor + "\n" +
                "  engine=" + engine + "\n" +
                "  wheel=" + wheel + "\n" +
                "  transmission=" + transmission + "\n";
    }
}
