/** Обробник для Microsoft Word. */
public class WordHandler extends FileHandler {
    @Override protected boolean canHandle(String extension) {
        return  extension.equals("doc") || extension.equals("docx");
    }
    @Override protected void launch(String fileName) {
        System.out.println("Opening '" + fileName + "' with Microsoft Word");
    }
}
