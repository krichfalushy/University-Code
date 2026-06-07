package com.customs;

public class Main {
    public static void main(String[] args) {
        double usdToUah = 41.5;
        double dutyRate = 0.10;   // 10%

        CustomsAdapter customs = new CustomsAdapter(usdToUah, dutyRate);

        Auto[] autos = {
            new Auto(3, "Toyota Camry", false, 60_000),
            new Auto(7, "Ford Truck",   true, 200_000),
            new Auto(2, "Honda Civic",  true,  30_000),
        };

        for (Auto auto : autos) {
            System.out.printf("Авто: %s (вік %d р., пробіг %d, пошкоджене: %s)%n",
                    auto.model, auto.age, auto.mileage, auto.damaged ? "так" : "ні");
            System.out.printf("  Вартість:          %,.2f грн%n", customs.vehiclePrice(auto));
            System.out.printf("  Мито:              %,.2f грн%n", customs.tax(auto));
            System.out.printf("  Після розмитнення: %,.2f грн%n%n", customs.fullPrice(auto));
        }
    }
}
