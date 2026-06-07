public class AmericanoFactory extends BeverageFactory {
    @Override public Beverage createBeverage() { return new Americano(); }
}
