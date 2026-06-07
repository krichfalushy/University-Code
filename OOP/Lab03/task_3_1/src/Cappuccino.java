public class Cappuccino extends Beverage {
    @Override protected void prepare() {
        ingredients.add(new Ingredient("Кава мелена", 6.0));
        ingredients.add(new Ingredient("Молоко", 4.0));
        ingredients.add(new Ingredient("Вода", 0.5));
    }
    @Override public String getName() { return "Капучіно"; }
    @Override public double getSalePrice() { return 40.0; }
}
