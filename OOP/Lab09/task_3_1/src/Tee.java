public class Tee extends Beverage {
    public Tee(int sugar, Preparation preparation, Serving serving) {
        super(sugar, preparation, serving);
    }
    @Override public String name() { return "tee"; }
    @Override public void putBase() { System.out.println("Put some tee..."); }
    @Override public int baseCost() { return 7; }
}
