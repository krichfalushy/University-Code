public class Engine {
    public enum Fuel { Petrol, Diesel, Electric }

    private final int power;     // потужність, к.с.
    private final int torque;    // крутний момент, Нм
    private final Fuel fuel;     // тип пального
    private final float volume;  // робочий об'єм, л

    private Engine(Builder b) {
        this.power = b.power;
        this.torque = b.torque;
        this.fuel = b.fuel;
        this.volume = b.volume;
    }

    @Override
    public String toString() {
        return "Engine{power=" + power + ", torque=" + torque +
                ", fuel=" + fuel + ", volume=" + volume + "}";
    }

    /** Будівельник двигуна. */
    public static class Builder {
        private int power;
        private int torque;
        private Fuel fuel = Fuel.Petrol;
        private float volume;

        public Builder power(int power)    { this.power = power; return this; }
        public Builder torque(int torque)  { this.torque = torque; return this; }
        public Builder fuel(Fuel fuel)     { this.fuel = fuel; return this; }
        public Builder volume(float volume){ this.volume = volume; return this; }

        public Engine build() { return new Engine(this); }
    }
}
