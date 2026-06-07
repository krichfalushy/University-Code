import com.mobile.Legacy.Charger;
import com.mobile.Legacy.MobilePhone;

public class Main {

    public static void main(String[] args) {
        Charger charger = new Charger(5.0f, 1.0f);   // micro-USB зарядка (5В, 1А = 5Вт)

        System.out.println("\n============= MicroUsb (рідний роз'єм) ==============\n");
        MobilePhone phone = new MobilePhone();
        phone.charge(20);
        phone.pluginCharger(charger);
        phone.charge(20);
        phone.unplugCharger();
        System.out.printf("Current power status = %s%n", phone.getChargingPercent());

        System.out.println("\n============= Type-C (рідний роз'єм) ==============\n");
        FastCharge fastCharge = new FastCharge(18);  // Type-C зарядка, 18 Вт
        SamsungS samsung = new SamsungS();
        samsung.makeCall();
        samsung.pluginCharger(fastCharge);
        samsung.charge(30);
        samsung.unplugCharger();
        printBatteryStatus(samsung);

        System.out.println("\n===== Адаптер micro-USB => Type-C (заряджаємо Samsung старою зарядкою) =====\n");
        // Спосіб 1: композиція
        TypeCCharger adapterComposition = new AdapterMicroUsbToTypeC(charger);
        samsung.pluginCharger(adapterComposition);
        samsung.charge(10);
        samsung.unplugCharger();
        printBatteryStatus(samsung);

        // Спосіб 2: наслідування
        TypeCCharger adapterInheritance = new AdapterMicroUsbToTypeCInheritance(5.0f, 1.0f);
        samsung.pluginCharger(adapterInheritance);
        samsung.charge(10);
        samsung.unplugCharger();
        printBatteryStatus(samsung);

        System.out.println("\n===== Зворотний адаптер Type-C => micro-USB (заряджаємо старий телефон новою зарядкою) =====\n");
        phone.pluginCharger(new AdapterTypeCToMicroUsb(fastCharge));
        phone.charge(20);
        phone.unplugCharger();
        System.out.printf("Current power status = %s%n", phone.getChargingPercent());
    }

    private static void printBatteryStatus(SamsungS phone) {
        System.out.printf("Current battery status = %s%n", phone.getChargingPercent());
    }
}
