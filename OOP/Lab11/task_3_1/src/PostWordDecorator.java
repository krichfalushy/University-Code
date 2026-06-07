/** Додає слово у кінець рядка. */
public class PostWordDecorator extends StringDecorator {
    private final String word;
    public PostWordDecorator(Printable wrappee, String word) {
        super(wrappee);
        this.word = word;
    }
    @Override public String getString() { return wrappee.getString() + word; }
}
