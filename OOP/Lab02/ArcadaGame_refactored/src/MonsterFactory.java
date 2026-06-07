/** Фабрика монстрів — виносить логіку створення зі сцени (SRP). */
public class MonsterFactory {
    public Monster create(MonsterType type, String level) {
        return switch (type) {
            case ZOMBIE    -> new Zombie(level);
            case IMP       -> new Imp(level);
            case DEMON     -> new Demon(level);
            case CACODEMON -> new Cacodemon(level);
        };
    }
}
