public class Main {
    public static void main(String[] args) {
        // Доступ до єдиного Директора
        Director director = Director.getInstance();

        System.out.println(director.buildEconomySedan(Car.CarColor.White));
        System.out.println(director.buildPremiumSUV(Car.CarColor.Black));
        System.out.println(director.buildElectricHatchback(Car.CarColor.Red));

        // Перевірка одинаковості
        Director director2 = Director.getInstance();
        System.out.println("Той самий Директор? " + (director == director2));
    }
}
