/** Конкретний творець 2 — повертає Product2. */
public class ConcreteCreator2 extends Creator {
    @Override
    public Product createProduct() {
        return new Product2();
    }
}
