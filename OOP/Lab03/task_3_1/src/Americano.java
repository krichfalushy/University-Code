public class Americano extends Beverage {
    @Override protected void prepare() {
        ingredients.add(new Ingredient("Кава мелена", 6.0));
        ingredients.add(new Ingredient("Вода", 1.0));
    }
    @Override public String getName() { return "Американо"; }
    @Override public double getSalePrice() { return 30.0; }
}
