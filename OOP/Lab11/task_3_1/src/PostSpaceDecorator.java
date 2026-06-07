/** Додає пропуск у кінець рядка. */
public class PostSpaceDecorator extends StringDecorator {
    public PostSpaceDecorator(Printable wrappee) { super(wrappee); }
    @Override public String getString() { return wrappee.getString() + " "; }
}
