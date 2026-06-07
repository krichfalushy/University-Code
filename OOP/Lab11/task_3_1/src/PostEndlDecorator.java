/** Додає перехід на новий рядок у кінець. */
public class PostEndlDecorator extends StringDecorator {
    public PostEndlDecorator(Printable wrappee) { super(wrappee); }
    @Override public String getString() { return wrappee.getString() + System.lineSeparator(); }
}
