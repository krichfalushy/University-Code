public class TextEditor {

    private Document document;

    private Button saveButton;
    private Button printButton;

    private MenuItem saveMenuItem;
    private MenuItem printMenuItem;
    private MenuItem showInfoMenuItem;

    private Shortcut saveShortcut;
    private Shortcut printShortcut;

    public TextEditor() {
        // інвокери створюються при відкритті/створенні документа (див. newDocument)
        saveButton = new Button();
        printButton = new Button();
        saveMenuItem = new MenuItem();
        printMenuItem = new MenuItem();
        showInfoMenuItem = new MenuItem();
        saveShortcut = new Shortcut();
        printShortcut = new Shortcut();
    }

    public void newDocument(String name) {
        document = new Document(name);

        // Прив'язуємо команди до елементів керування.
        // Одна й та сама команда може використовуватись різними інвокерами.
        Command save = new SaveCommand(document);
        Command print = new PrintCommand(document);
        Command showInfo = new ShowInfoCommand(document);

        saveButton.setCommand(save);
        printButton.setCommand(print);

        saveMenuItem.setCommand(save);
        printMenuItem.setCommand(print);
        showInfoMenuItem.setCommand(showInfo);

        saveShortcut.setCommand(save);
        printShortcut.setCommand(print);
    }

    public void clickSaveButton()        { saveButton.click(); }
    public void clickPrintButton()       { printButton.click(); }
    public void selectSaveMenuItem()     { saveMenuItem.select(); }
    public void selectPrintMenuItem()    { printMenuItem.select(); }
    public void selectShowInfoMenuItem() { showInfoMenuItem.select(); }
    public void pressSaveShortcut()      { saveShortcut.press(); }
    public void pressPrintShortcut()     { printShortcut.press(); }
}
