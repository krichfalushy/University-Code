public class Latte extends Beverage {
    @Override protected void prepare() {
        ingredients.add(new Ingredient("Кава мелена", 6.0));
        ingredients.add(new Ingredient("Молоко", 7.0));
        ingredients.add(new Ingredient("Вода", 0.5));
    }
    @Override public String getName() { return "Латте"; }
    @Override public double getSalePrice() { return 45.0; }
}
