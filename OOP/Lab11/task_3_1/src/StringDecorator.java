/**
 * Базовий декоратор. Обгортає інший Printable та делегує йому виклик,
 * а нащадки модифікують результат getString().
 */
public abstract class StringDecorator implements Printable {
    protected final Printable wrappee;

    protected StringDecorator(Printable wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public void print() {
        System.out.print(getString());
    }
}
