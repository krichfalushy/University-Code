/** Конкретний компонент — контейнер для рядка. */
public class PrintableString implements Printable {
    private final String base;

    public PrintableString(String base) {
        this.base = base;
    }

    @Override
    public String getString() {
        return base;
    }

    @Override
    public void print() {
        System.out.print(base);
    }
}
