/**
 * Базовий декоратор наповнювача. Сам є Beverage і обгортає інший Beverage,
 * розширюючи його опис та вартість.
 */
public abstract class CondimentDecorator extends Beverage {
    protected final Beverage beverage;

    protected CondimentDecorator(Beverage beverage) {
        this.beverage = beverage;
    }
}
