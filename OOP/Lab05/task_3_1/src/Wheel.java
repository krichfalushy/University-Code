public class Wheel {
    public enum Material { Steel, Alloy, Forged }

    private final Material material;  // матеріал диску
    private final int diameter;       // діаметр, дюйми

    private Wheel(Builder b) {
        this.material = b.material;
        this.diameter = b.diameter;
    }

    @Override
    public String toString() {
        return "Wheel{material=" + material + ", diameter=" + diameter + "}";
    }

    /** Будівельник колісних дисків. */
    public static class Builder {
        private Material material = Material.Steel;
        private int diameter = 15;

        public Builder material(Material material) { this.material = material; return this; }
        public Builder diameter(int diameter)      { this.diameter = diameter; return this; }

        public Wheel build() { return new Wheel(this); }
    }
}
