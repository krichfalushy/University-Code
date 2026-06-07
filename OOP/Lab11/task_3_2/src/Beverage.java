/** Компонент (Component) шаблону Декоратор. */
public abstract class Beverage {
    public abstract String description();
    public abstract float cost();

    @Override
    public String toString() {
        return "Beverage: " + description() + ", $" + String.format("%.2f", cost());
    }
}
