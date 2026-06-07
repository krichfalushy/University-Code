/** Розширення асортименту — новий напій додається без зміни наявного коду. */
public class HotChocolate extends Beverage {
    @Override protected void prepare() {
        ingredients.add(new Ingredient("Какао", 8.0));
        ingredients.add(new Ingredient("Молоко", 7.0));
        ingredients.add(new Ingredient("Цукор", 1.5));
    }
    @Override public String getName() { return "Гарячий шоколад"; }
    @Override public double getSalePrice() { return 50.0; }
}
