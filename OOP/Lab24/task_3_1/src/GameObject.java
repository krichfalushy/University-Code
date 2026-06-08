/**
 * Базовий персонаж аркади. Містить ШАБЛОННИЙ МЕТОД defendAgainstAttack(),
 * що задає незмінний скелет алгоритму захисту, а конкретні кроки
 * делегує нащадкам.
 */
public abstract class GameObject {

    /** Підняти зброю (крок алгоритму). */
    protected abstract void pickUpWeapon();

    /** Виконати захисну дію (крок алгоритму). */
    protected abstract void defenseAction();

    /** Відступити в безпечне місце (крок алгоритму). */
    protected abstract void moveToSafety();

    /**
     * Шаблонний метод: незмінна послідовність кроків захисту.
     * Оголошений final, щоб нащадки не змінювали сам алгоритм.
     */
    public final void defendAgainstAttack() {
        pickUpWeapon();
        defenseAction();
        moveToSafety();
        System.out.println();
    }
}
