/** Обробник для Microsoft PowerPoint. */
public class PowerPointHandler extends FileHandler {
    @Override protected boolean canHandle(String extension) {
        return  extension.equals("ppt") || extension.equals("pptx");
    }
    @Override protected void launch(String fileName) {
        System.out.println("Opening '" + fileName + "' with Microsoft PowerPoint");
    }
}
