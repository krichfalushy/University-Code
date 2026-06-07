/**
 * Абстракція (Abstraction) мосту — кнопка.
 * Містить посилання на Size (реалізатор) — це "міст".
 */
public abstract class Button {
    protected final Size size;   // МІСТ до реалізатора

    protected Button(Size size) {
        this.size = size;
    }

    /** Малювання конкретного типу кнопки. */
    protected abstract void render();

    /** Спільний алгоритм: спершу застосувати розмір, потім намалювати. */
    public void draw() {
        size.applySize();
        render();
    }
}
