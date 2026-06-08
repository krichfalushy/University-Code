public class Main {
    public static void main(String[] args) {

        ControlTower tower = new ControlTower();

        Plane plane123 = new Plane(123);
        Plane plane456 = new Plane(456);

        tower.register(plane123);
        tower.register(plane456);

        plane123.takeOff();
        plane456.takeOff();

        System.out.println("\nIn flight: " + tower.planesInFlightCount()
                + ", on ground: " + tower.planesOnGroundCount());

        System.out.println();
        plane123.land();
        plane456.land();

        System.out.println("\nIn flight: " + tower.planesInFlightCount()
                + ", on ground: " + tower.planesOnGroundCount());
    }
}
