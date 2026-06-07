/**
 * Контекст. Зберігає унікальний (зовнішній) стан — тип і колір,
 * та посилається на спільні легковаговики Engine та Wheel.
 */
public class Car {
    public enum CarColor { White, Black, Red, Grey }
    public enum Type { Sedan, Hatchback, SUV }

    final private Type type;
    final private CarColor carColor;
    final private Engine engine;   // спільний flyweight
    final private Wheel wheel;     // спільний flyweight

    public Car(Type type, CarColor carColor, Engine engine, Wheel wheel) {
        this.type = type;
        this.carColor = carColor;
        this.engine = engine;
        this.wheel = wheel;
    }

    public Engine getEngine() { return engine; }
    public Wheel getWheel() { return wheel; }

    public void showInfo() {
        System.out.println("Car:\n" +
                "type=" + type +
                ",\ncarColor=" + carColor +
                ",\nengine=" + engine +
                ",\nwheel=" + wheel + "\n");
    }

    @Override
    public String toString() {
        return "Car{type=" + type + ", color=" + carColor + "}";
    }
}
