/** Спосіб подачі напою. */
public enum Serving {
    DINE_IN("for dine-in (in a cup)"),
    TAKEAWAY("for takeaway (in a paper cup)");

    private final String description;
    Serving(String description) { this.description = description; }
    public String getDescription() { return description; }

    /** Надбавка за одноразовий стакан на винос. */
    public int extraCost() { return this == TAKEAWAY ? 2 : 0; }
}
