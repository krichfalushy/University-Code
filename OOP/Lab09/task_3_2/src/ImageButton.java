/** Нова уточнена абстракція — графічна кнопка із зображенням. */
public class ImageButton extends Button {
    private final String imagePath;

    public ImageButton(Size size, String imagePath) {
        super(size);
        this.imagePath = imagePath;
    }

    @Override protected void render() {
        System.out.printf("Drawing an image button with image '%s'.%n%n", imagePath);
    }
}
