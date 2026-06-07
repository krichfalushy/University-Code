import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика легковаговиків для двигунів.
 * Кешує створені двигуни за ключем (power|fuel) і повертає той самий
 * екземпляр для однакових параметрів.
 */
public class EngineFactory {
    private static final Map<String, Engine> cache = new HashMap<>();

    public static Engine getEngine(int power, Engine.Fuel fuel) {
        String key = power + "|" + fuel;
        Engine engine = cache.get(key);
        if (engine == null) {
            engine = new Engine(power, fuel);
            cache.put(key, engine);
            System.out.println("  [EngineFactory] created new " + engine);
        }
        return engine;
    }

    public static int cachedCount() { return cache.size(); }
}
