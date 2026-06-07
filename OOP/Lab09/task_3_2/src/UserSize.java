/** Новий реалізатор — користувацький розмір (ширина × висота). */
public class UserSize implements Size {
    private final int width;
    private final int height;

    public UserSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override public void applySize() {
        System.out.printf("Setting custom size to %dx%d...%n", width, height);
    }
}
