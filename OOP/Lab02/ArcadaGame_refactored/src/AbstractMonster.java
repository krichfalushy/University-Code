/** Базова реалізація: усуває дублювання level/конструктора/shoot() (DRY). */
public abstract class AbstractMonster implements Monster {
    protected final String level;

    protected AbstractMonster(String level) {
        this.level = level;
    }

    @Override
    public void shoot() {
        System.out.printf("Creature: %s, level %s, action: shoots%n",
                getClass().getSimpleName(), level);
    }
}
