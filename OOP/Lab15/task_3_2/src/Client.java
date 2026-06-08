public class Client {
    public static void main(String[] args) {

        String[] files = {
                "image.jpg",
                "image.png",
                "document.docx",
                "document.doc",
                "table.xls",
                "table.xlsx",
                "presentation.pptx",
                "document.pdf",
                "archive.zip",   // невідоме розширення -> ніхто не обробить
        };

        // Ланцюг: Image -> Word -> Excel -> PowerPoint -> PDF
        FileHandler chain = new ImageHandler();
        chain.linkWith(new WordHandler())
             .linkWith(new ExcelHandler())
             .linkWith(new PowerPointHandler())
             .linkWith(new PdfHandler());

        for (String file : files) {
            chain.open(file);
        }
    }
}
