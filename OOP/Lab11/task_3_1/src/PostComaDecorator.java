/** Додає кому в кінець рядка. */
public class PostComaDecorator extends StringDecorator {
    public PostComaDecorator(Printable wrappee) { super(wrappee); }
    @Override public String getString() { return wrappee.getString() + ","; }
}
