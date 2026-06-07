/** Додає знак оклику у кінець рядка. */
public class PostExclaimDecorator extends StringDecorator {
    public PostExclaimDecorator(Printable wrappee) { super(wrappee); }
    @Override public String getString() { return wrappee.getString() + "!"; }
}
