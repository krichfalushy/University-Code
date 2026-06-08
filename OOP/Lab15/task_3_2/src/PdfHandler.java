/** Обробник для PDF Reader. */
public class PdfHandler extends FileHandler {
    @Override protected boolean canHandle(String extension) {
        return  extension.equals("pdf");
    }
    @Override protected void launch(String fileName) {
        System.out.println("Opening '" + fileName + "' with PDF Reader");
    }
}
