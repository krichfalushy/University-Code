/**
 * Будівельник рядка. Дозволяє покроково формувати рядок:
 *  - append(...) — додати послідовність символів у кінець;
 *  - insert(pos, ...) — вставити послідовність символів у вказану позицію.
 * Кожен метод повертає this (fluent interface), build() — готовий рядок.
 */
public class StringBuilderEx {

    private char[] buffer;
    private int length;   // фактична кількість символів

    public StringBuilderEx() {
        this.buffer = new char[16];
        this.length = 0;
    }

    public StringBuilderEx(String initial) {
        this();
        append(initial);
    }

    /** Додати послідовність символів у кінець. */
    public StringBuilderEx append(CharSequence seq) {
        if (seq == null) seq = "null";
        ensureCapacity(length + seq.length());
        for (int i = 0; i < seq.length(); i++) {
            buffer[length++] = seq.charAt(i);
        }
        return this;
    }

    /** Додати один символ у кінець. */
    public StringBuilderEx append(char c) {
        ensureCapacity(length + 1);
        buffer[length++] = c;
        return this;
    }

    /**
     * Вставити послідовність символів у вказану позицію.
     * @param pos позиція вставки (0..length)
     */
    public StringBuilderEx insert(int pos, CharSequence seq) {
        if (pos < 0 || pos > length) {
            throw new IndexOutOfBoundsException("pos=" + pos + ", length=" + length);
        }
        if (seq == null) seq = "null";
        int n = seq.length();
        ensureCapacity(length + n);
        // зсуваємо хвіст праворуч на n позицій
        System.arraycopy(buffer, pos, buffer, pos + n, length - pos);
        // записуємо вставлені символи
        for (int i = 0; i < n; i++) {
            buffer[pos + i] = seq.charAt(i);
        }
        length += n;
        return this;
    }

    /** Поточна довжина рядка. */
    public int length() { return length; }

    /** Завершення побудови — повертає готовий рядок. */
    public String build() {
        return new String(buffer, 0, length);
    }

    @Override
    public String toString() { return build(); }

    /** Збільшення внутрішнього буфера за потреби. */
    private void ensureCapacity(int needed) {
        if (needed <= buffer.length) return;
        int newCap = Math.max(buffer.length * 2, needed);
        char[] grown = new char[newCap];
        System.arraycopy(buffer, 0, grown, 0, length);
        buffer = grown;
    }
}
