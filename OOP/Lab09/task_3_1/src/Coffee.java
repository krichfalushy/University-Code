public class Coffee extends Beverage {
    public Coffee(int sugar, Preparation preparation, Serving serving) {
        super(sugar, preparation, serving);
    }
    @Override public String name() { return "coffee"; }
    @Override public void putBase() { System.out.println("Put some coffee..."); }
    @Override public int baseCost() { return 10; }
}
