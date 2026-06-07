public class Main {
    public static void main(String[] args) {
        CarBuilder builder = new CarBuilder();
        CarSimulator simulator = new CarSimulator(builder);

        Car car = simulator.constructCar();
        System.out.println(car);
    }
}
