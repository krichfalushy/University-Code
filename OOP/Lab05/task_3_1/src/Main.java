public class Main {
    public static void main(String[] args) {
        Director director = new Director();

        System.out.println("=== Через Директора (готові класи авто) ===");
        System.out.println(director.buildEconomySedan(Car.CarColor.White));
        System.out.println(director.buildPremiumSUV(Car.CarColor.Black));
        System.out.println(director.buildElectricHatchback(Car.CarColor.Red));

        System.out.println("=== Ручне складання через будівельники ===");
        Car custom = new Car.Builder()
                .type(Car.Type.Sedan)
                .color(Car.CarColor.Grey)
                .engine(new Engine.Builder().power(180).torque(300).fuel(Engine.Fuel.Petrol).volume(2.0f).build())
                .wheel(new Wheel.Builder().material(Wheel.Material.Alloy).diameter(18).build())
                .transmission(new Transmission.Builder().type(Transmission.Type.Automatic).gears(6).build())
                .build();
        System.out.println(custom);
    }
}
