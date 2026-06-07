public class Chocolate extends Beverage {
    public Chocolate(int sugar, Preparation preparation, Serving serving) {
        super(sugar, preparation, serving);
    }
    @Override public String name() { return "chocolate"; }
    @Override public void putBase() { System.out.println("Put some cacao..."); }
    @Override public int baseCost() { return 15; }
}
