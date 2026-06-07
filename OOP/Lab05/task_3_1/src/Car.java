public class Car {
    public enum CarColor { White, Black, Red, Grey }
    public enum Type { Sedan, Hatchback, SUV }

    private final Type type;
    private final CarColor carColor;
    private final Engine engine;
    private final Wheel wheel;
    private final Transmission transmission;

    private Car(Builder b) {
        this.type = b.type;
        this.carColor = b.carColor;
        this.engine = b.engine;
        this.wheel = b.wheel;
        this.transmission = b.transmission;
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

    /**
     * Головний будівельник автомобіля. Приймає готові складові частини
     * (кожна з яких будується власним будівельником).
     */
    public static class Builder {
        private Type type;
        private CarColor carColor;
        private Engine engine;
        private Wheel wheel;
        private Transmission transmission;

        public Builder type(Type type)               { this.type = type; return this; }
        public Builder color(CarColor carColor)       { this.carColor = carColor; return this; }
        public Builder engine(Engine engine)          { this.engine = engine; return this; }
        public Builder wheel(Wheel wheel)             { this.wheel = wheel; return this; }
        public Builder transmission(Transmission t)   { this.transmission = t; return this; }

        public Car build() { return new Car(this); }
    }
}
