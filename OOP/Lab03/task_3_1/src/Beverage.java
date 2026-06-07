import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактний продукт — напій.
 * Собівартість = сума вартостей компонентів; ціна продажу задається окремо.
 */
public abstract class Beverage {
    protected final List<Ingredient> ingredients = new ArrayList<>();

    protected Beverage() {
        prepare();           // наповнення компонентами під час "приготування"
    }

    /** Кожен напій формує свій склад компонентів. */
    protected abstract void prepare();

    /** Назва напою. */
    public abstract String getName();

    /** Ціна продажу клієнту, грн. */
    public abstract double getSalePrice();

    /** Собівартість = сума вартостей усіх компонентів. */
    public double getCost() {
        double sum = 0;
        for (Ingredient i : ingredients) {
            sum += i.getCost();
        }
        return sum;
    }

    /** Прибуток з одного напою. */
    public double getProfit() {
        return getSalePrice() - getCost();
    }

    @Override
    public String toString() {
        return String.format("%-10s | собівартість: %5.2f | ціна: %5.2f | прибуток: %5.2f",
                getName(), getCost(), getSalePrice(), getProfit());
    }
}
