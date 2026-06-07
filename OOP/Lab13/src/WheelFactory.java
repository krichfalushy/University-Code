import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика легковаговиків для коліс.
 * Кешує колеса за діаметром і повертає спільний екземпляр.
 */
public class WheelFactory {
    private static final Map<Integer, Wheel> cache = new HashMap<>();

    public static Wheel getWheel(int diameter) {
        Wheel wheel = cache.get(diameter);
        if (wheel == null) {
            wheel = new Wheel(diameter);
            cache.put(diameter, wheel);
            System.out.println("  [WheelFactory] created new " + wheel);
        }
        return wheel;
    }

    public static int cachedCount() { return cache.size(); }
}
