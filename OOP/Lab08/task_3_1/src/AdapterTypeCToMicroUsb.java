import com.mobile.Legacy.MicroUsbCharger;

/**
 * Зворотний адаптер: Type-C => micro-USB.
 * Дозволяє заряджати застарілий MobilePhone (роз'єм micro-USB)
 * через сучасну Type-C зарядку.
 *
 * Проблема перетворення: Type-C надає лише сумарну потужність (getOutputPower),
 * а micro-USB-контракт вимагає окремо напругу та силу струму.
 * Тому доводиться доозброювати адаптер припущенням про номінальну напругу
 * (стандарт micro-USB = 5 В) і обчислювати струм як I = P / U.
 */
public class AdapterTypeCToMicroUsb implements MicroUsbCharger {

    private static final float NOMINAL_VOLTAGE = 5.0f;  // припущення micro-USB

    private final TypeCCharger typeCCharger;

    public AdapterTypeCToMicroUsb(TypeCCharger typeCCharger) {
        this.typeCCharger = typeCCharger;
    }

    @Override
    public float getOutputVoltage() {
        return NOMINAL_VOLTAGE;
    }

    @Override
    public float getOutputAmperage() {
        // I = P / U
        return typeCCharger.getOutputPower() / NOMINAL_VOLTAGE;
    }
}
