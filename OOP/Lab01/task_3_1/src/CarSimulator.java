/**
 * Розпорядник (Director). Композиційно володіє CarBuilder (ромб 1)
 * та набором Wheel (ромб 1). Керує послідовністю побудови машини.
 */
public class CarSimulator {
    private final CarBuilder builder;

    public CarSimulator(CarBuilder builder) {
        this.builder = builder;
    }

    public Car constructCar() {
        builder.createNewCar();
        builder.buildEngine("V6");
        builder.buildWheels(4, 18);
        return builder.getCar();
    }
}
