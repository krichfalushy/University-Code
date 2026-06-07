public class Main {
    public static void main(String[] args) {
        // Базовий прототип авто
        Car prototype = new Car(
                Car.Type.Sedan, Car.CarColor.White,
                new Engine(105, 140, Engine.Fuel.Petrol, 1.4f),
                new Wheel(Wheel.Material.Steel, 15),
                new Transmission(Transmission.Type.Manual, 5));

        System.out.println("=== Прототип ===");
        System.out.println(prototype);

        // Клонуємо й модифікуємо клон під іншу комплектацію
        Car clone = prototype.clone();
        clone.setColor(Car.CarColor.Black);
        clone.getEngine().setPower(150);
        clone.getEngine().setFuel(Engine.Fuel.Diesel);
        clone.getWheel().setDiameter(18);
        clone.getTransmission().setGears(6);

        System.out.println("=== Клон (модифікований) ===");
        System.out.println(clone);

        // Доводимо незалежність: оригінал не змінився
        System.out.println("=== Прототип після зміни клона (має лишитись незмінним) ===");
        System.out.println(prototype);

        // Перевірка, що компоненти — РІЗНІ об'єкти (глибока копія)
        System.out.println("engine той самий об'єкт? "
                + (prototype.getEngine() == clone.getEngine()));
        System.out.println("wheel той самий об'єкт? "
                + (prototype.getWheel() == clone.getWheel()));
        System.out.println("transmission той самий об'єкт? "
                + (prototype.getTransmission() == clone.getTransmission()));
    }
}
