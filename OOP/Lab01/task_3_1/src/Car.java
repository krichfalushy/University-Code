import java.util.ArrayList;
import java.util.List;

/**
 * Продукт, який будується. Car композиційно володіє Engine та своїми Wheel
 * (на діаграмі — зафарбовані ромби з боку Car).
 */
public class Car {
    private Engine engine;
    private final List<Wheel> wheels = new ArrayList<>();

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void addWheel(Wheel wheel) {
        wheels.add(wheel);
    }

    public Engine getEngine() {
        return engine;
    }

    public List<Wheel> getWheels() {
        return wheels;
    }

    @Override
    public String toString() {
        return "Car{engine=" + engine + ", wheels=" + wheels + "}";
    }
}
