public class Transmission {
    public enum Type { Manual, Automatic }

    private final Type type;     // механічна / автоматична
    private final int gears;     // кількість передач

    private Transmission(Builder b) {
        this.type = b.type;
        this.gears = b.gears;
    }

    @Override
    public String toString() {
        return "Transmission{type=" + type + ", gears=" + gears + "}";
    }

    /** Будівельник трансмісії. */
    public static class Builder {
        private Type type = Type.Manual;
        private int gears = 5;

        public Builder type(Type type)   { this.type = type; return this; }
        public Builder gears(int gears)   { this.gears = gears; return this; }

        public Transmission build() { return new Transmission(this); }
    }
}
