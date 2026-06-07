import java.util.ArrayList;
import java.util.List;

/** Ігрова сцена: один поліморфний список, жодних switch/окремих списків. */
public class GameScene {

    private final String level;
    private final MonsterFactory factory = new MonsterFactory();
    private final List<Monster> monsters = new ArrayList<>();

    public GameScene(String level) {
        this.level = level;
    }

    public void addMonster(MonsterType type) {
        monsters.add(factory.create(type, level));
    }

    public void start() {
        for (Monster monster : monsters) {
            monster.shoot();
        }
    }
}
