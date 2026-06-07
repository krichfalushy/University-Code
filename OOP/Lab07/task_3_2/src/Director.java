/**
 * Директор як Одинак (Singleton): у програмі може існувати
 * лише один Директор автомобільного заводу.
 * Поєднує процес виготовлення авто, використовуючи будівельники складових.
 */
public class Director {

    // Eager-ініціалізація єдиного екземпляра.
    private static final Director instance = new Director();

    // Приватний конструктор — заборона створення інших Директорів.
    private Director() {
    }

    public static Director getInstance() {
        return instance;
    }

    /** Бюджетний седан. */
    public Car buildEconomySedan(Car.CarColor color) {
        Engine engine = new Engine.Builder()
                .power(105).torque(140).fuel(Engine.Fuel.Petrol).volume(1.4f).build();
        Wheel wheel = new Wheel.Builder()
                .material(Wheel.Material.Steel).diameter(15).build();
        Transmission transmission = new Transmission.Builder()
                .type(Transmission.Type.Manual).gears(5).build();
        return new Car.Builder()
                .type(Car.Type.Sedan).color(color)
                .engine(engine).wheel(wheel).transmission(transmission)
                .build();
    }

    /** Преміальний SUV. */
    public Car buildPremiumSUV(Car.CarColor color) {
        Engine engine = new Engine.Builder()
                .power(300).torque(450).fuel(Engine.Fuel.Diesel).volume(3.0f).build();
        Wheel wheel = new Wheel.Builder()
                .material(Wheel.Material.Forged).diameter(20).build();
        Transmission transmission = new Transmission.Builder()
                .type(Transmission.Type.Automatic).gears(8).build();
        return new Car.Builder()
                .type(Car.Type.SUV).color(color)
                .engine(engine).wheel(wheel).transmission(transmission)
                .build();
    }

    /** Електричний хетчбек. */
    public Car buildElectricHatchback(Car.CarColor color) {
        Engine engine = new Engine.Builder()
                .power(150).torque(260).fuel(Engine.Fuel.Electric).volume(0.0f).build();
        Wheel wheel = new Wheel.Builder()
                .material(Wheel.Material.Alloy).diameter(17).build();
        Transmission transmission = new Transmission.Builder()
                .type(Transmission.Type.Automatic).gears(1).build();
        return new Car.Builder()
                .type(Car.Type.Hatchback).color(color)
                .engine(engine).wheel(wheel).transmission(transmission)
                .build();
    }
}
