/** Обробник для Microsoft Excel. */
public class ExcelHandler extends FileHandler {
    @Override protected boolean canHandle(String extension) {
        return  extension.equals("xls") || extension.equals("xlsx");
    }
    @Override protected void launch(String fileName) {
        System.out.println("Opening '" + fileName + "' with Microsoft Excel");
    }
}
