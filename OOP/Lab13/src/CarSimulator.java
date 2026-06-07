import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarSimulator {

    static List<Car> vehicles = new ArrayList<>();
    static final CarBuilder carBuilder = new CarBuilder();

    public static void main(String[] args) {
        final int N = 20;                 // кількість авто
        Random random = new Random(42);   // фіксований seed для відтворюваності

        System.out.println("=== Creating " + N + " cars ===");
        for (int i = 0; i < N; i++) {
            createRandomCar(random);
        }

        System.out.println("\n=== Summary ===");
        System.out.println("Cars created:           " + vehicles.size());
        System.out.println("Unique Engine objects:  " + EngineFactory.cachedCount());
        System.out.println("Unique Wheel objects:   " + WheelFactory.cachedCount());

        // Перевірка спільного використання: підраховуємо унікальні екземпляри за посиланнями
        long distinctEngines = vehicles.stream().map(Car::getEngine).distinct().count();
        long distinctWheels  = vehicles.stream().map(Car::getWheel).distinct().count();
        System.out.println("Distinct engine refs used: " + distinctEngines
                + " (instead of up to " + vehicles.size() + ")");
        System.out.println("Distinct wheel refs used:  " + distinctWheels
                + " (instead of up to " + vehicles.size() + ")");
    }

    private static void createRandomCar(Random random) {
        Engine.Fuel fuel = Engine.Fuel.values()[random.nextInt(3)];
        int wheelDiameter = 17 + random.nextInt(3);   // 17..19
        int power = (11 + random.nextInt(5)) * 10;     // 110..150
        Car.CarColor color = Car.CarColor.values()[random.nextInt(4)];

        Car car = carBuilder
                .reset()
                .setCarColor(color)
                .setEngine(power, fuel)
                .setWheel(wheelDiameter)
                .build();

        vehicles.add(car);
    }
}
