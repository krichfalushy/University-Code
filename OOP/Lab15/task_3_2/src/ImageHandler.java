/** Обробник для Image Viewer. */
public class ImageHandler extends FileHandler {
    @Override protected boolean canHandle(String extension) {
        return  extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif") || extension.equals("bmp");
    }
    @Override protected void launch(String fileName) {
        System.out.println("Opening '" + fileName + "' with Image Viewer");
    }
}
