/** Додає слово на початок рядка. */
public class PreWordDecorator extends StringDecorator {
    private final String word;
    public PreWordDecorator(Printable wrappee, String word) {
        super(wrappee);
        this.word = word;
    }
    @Override public String getString() { return word + wrappee.getString(); }
}
