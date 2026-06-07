/**
 * Абстракція (Abstraction) мосту — напій.
 * Містить посилання на Preparation (реалізатор) — це і є "міст".
 * Додатково має вимір Serving (в ресторані / на винос).
 */
public abstract class Beverage {

    protected final int sugar;
    protected final Preparation preparation;   // МІСТ до реалізатора
    protected final Serving serving;

    protected Beverage(int sugar, Preparation preparation, Serving serving) {
        this.sugar = sugar;
        this.preparation = preparation;
        this.serving = serving;
    }

    /** Базова назва напою (кава/чай/шоколад). */
    public abstract String name();
    /** Додати основу напою. */
    public abstract void putBase();
    /** Базова вартість напою. */
    public abstract int baseCost();

    /** Спільний алгоритм приготування — однаковий для всіх напоїв. */
    public void prepare() {
        System.out.printf("Preparing %s %s, %s%n", preparation.description(), name(),
                serving.getDescription());
        putBase();                         // основа (залежить від типу напою)
        preparation.addLiquid();           // рідина (залежить від реалізатора)
        if (sugar > 0) {
            System.out.printf("Put some sugar: %s pieces...%n", sugar);
        }
        if (serving == Serving.TAKEAWAY) {
            System.out.println("Pour into a paper cup and close the lid...");
        }
    }

    /** Підсумкова вартість: основа + наповнювач + подача. */
    public int cost() {
        return baseCost() + preparation.extraCost() + serving.extraCost();
    }

    public void drink() {
        System.out.printf("Enjoy your %s %s!%n", preparation.description(), name());
    }
}
