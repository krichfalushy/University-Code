/** Конкретний творець 1 — повертає Product1. */
public class ConcreteCreator1 extends Creator {
    @Override
    public Product createProduct() {
        return new Product1();
    }
}
