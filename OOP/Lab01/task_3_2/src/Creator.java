/**
 * Творець (Creator). Містить фабричний метод createProduct(),
 * який перевизначають нащадки, та бізнес-логіку someOperation(),
 * що працює з продуктом через інтерфейс Product.
 */
public abstract class Creator {

    public abstract Product createProduct();

    public void someOperation() {
        Product p = createProduct();
        p.doSomething();
    }
}
