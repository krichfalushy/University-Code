public class Espresso extends Beverage {
    @Override protected void prepare() {
        ingredients.add(new Ingredient("Кава мелена", 6.0));
        ingredients.add(new Ingredient("Вода", 0.5));
    }
    @Override public String getName() { return "Еспресо"; }
    @Override public double getSalePrice() { return 25.0; }
}
