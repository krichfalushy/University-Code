package com.customs;

import com.vehiclecalculator.Car;
import com.vehiclecalculator.CarCalculator;
import com.vehiclecalculator.Truck;
import com.vehiclecalculator.TruckCalculator;
import com.vehiclecalculator.Vehicle;
import com.vehiclecalculator.VehicleCalculator;

/**
 * Адаптер. Дозволяє українській митниці (інтерфейс Customs, гривні)
 * використовувати готовий американський Автомобільний калькулятор
 * (VehicleCalculator, долари США), що постачається у вигляді бібліотеки.
 *
 * Розміщений у пакеті com.customs, бо клас Auto є package-private.
 *
 * Що адаптує:
 *   1) Тип даних: Customs оперує Auto, калькулятор — Vehicle.
 *   2) Формат результату: calculatePrice() повертає рядок виду "570USD".
 *   3) Валюту: долари -> гривні за курсом.
 *   4) Додає мито (tax), якого в калькуляторі немає.
 */
public class CustomsAdapter implements Customs {

    private final double usdToUah;   // курс долара
    private final double dutyRate;    // ставка мита (частка від вартості авто)

    public CustomsAdapter(double usdToUah, double dutyRate) {
        this.usdToUah = usdToUah;
        this.dutyRate = dutyRate;
    }

    /** Вартість авто в гривнях (адаптована з калькулятора). */
    @Override
    public float vehiclePrice(Auto auto) {
        double priceUsd = calculatePriceUsd(auto);
        return (float) (priceUsd * usdToUah);
    }

    /** Розмір мита в гривнях. */
    @Override
    public float tax(Auto auto) {
        return (float) (vehiclePrice(auto) * dutyRate);
    }

    /** Повна вартість авто після розмитнення (вартість + мито), грн. */
    public float fullPrice(Auto auto) {
        return vehiclePrice(auto) + tax(auto);
    }

    // --- внутрішня логіка адаптації ---

    private double calculatePriceUsd(Auto auto) {
        VehicleCalculator calculator = createCalculator(auto);
        calculator.setVehicle(toVehicle(auto));
        String result = calculator.calculatePrice();   // напр. "570USD"
        return parseUsd(result);
    }

    /** Обираємо потрібний конкретний калькулятор за типом авто. */
    private VehicleCalculator createCalculator(Auto auto) {
        if (isTruck(auto)) {
            return new TruckCalculator();
        }
        return new CarCalculator();
    }

    /** Перетворення Auto (митниця) -> Vehicle (калькулятор). */
    private Vehicle toVehicle(Auto auto) {
        // boolean -> float: у калькуляторі damage = частка збереженої вартості
        // (1.0 = ціле авто за повну ціну, менше = є пошкодження).
        float damage = auto.damaged ? 0.5f : 1.0f;
        if (isTruck(auto)) {
            return new Truck(auto.age, auto.mileage);
        }
        return new Car(auto.age, auto.model, damage);
    }

    private boolean isTruck(Auto auto) {
        return auto.model != null && auto.model.toLowerCase().contains("truck");
    }

    /** Розбір рядка виду "570USD" -> 570.0 */
    private double parseUsd(String s) {
        String digits = s.replaceAll("[^0-9.\\-]", "");
        return digits.isEmpty() ? 0.0 : Double.parseDouble(digits);
    }
}
