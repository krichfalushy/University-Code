public class Wheel {
    private final int radius;

    public Wheel(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Wheel{radius=" + radius + "}";
    }
}
