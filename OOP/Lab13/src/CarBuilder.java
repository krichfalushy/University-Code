public class CarBuilder {

    private Car.Type type;
    private Car.CarColor carColor;
    private Engine engine;
    private Wheel wheel;

    public CarBuilder() { reset(); }

    public CarBuilder setType(Car.Type type) { this.type = type; return this; }
    public CarBuilder setCarColor(Car.CarColor carColor) { this.carColor = carColor; return this; }

    /** Двигун беремо з кешу легковаговиків. */
    public CarBuilder setEngine(int power, Engine.Fuel fuel) {
        this.engine = EngineFactory.getEngine(power, fuel);
        return this;
    }

    /** Колесо беремо з кешу легковаговиків. */
    public CarBuilder setWheel(int diameter) {
        this.wheel = WheelFactory.getWheel(diameter);
        return this;
    }

    public CarBuilder reset() {
        type = Car.Type.Sedan;
        carColor = Car.CarColor.White;
        engine = EngineFactory.getEngine(105, Engine.Fuel.Petrol);
        wheel = WheelFactory.getWheel(17);
        return this;
    }

    public Car build() {
        if (type == null || carColor == null || engine == null || wheel == null) {
            throw new IllegalArgumentException();
        }
        return new Car(type, carColor, engine, wheel);
    }
}
