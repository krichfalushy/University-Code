/** Наповнювач — цукор (порція). */
public class Sugar extends CondimentDecorator {
    public Sugar(Beverage beverage) { super(beverage); }
    @Override public String description() { return beverage.description() + ", Sugar"; }
    @Override public float cost() { return beverage.cost() + 0.05f; }
}
