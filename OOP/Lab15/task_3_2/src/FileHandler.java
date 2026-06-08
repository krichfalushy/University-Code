/**
 * Базовий обробник ланцюга для відкриття файлів.
 * Кожен обробник відкриває файли "своїх" розширень або передає далі.
 */
public abstract class FileHandler {

    private FileHandler next;

    public FileHandler linkWith(FileHandler next) {
        this.next = next;
        return next;
    }

    /** Спробувати відкрити файл. */
    public void open(String fileName) {
        String ext = extension(fileName);
        if (canHandle(ext)) {
            launch(fileName);
        } else if (next != null) {
            next.open(fileName);
        } else {
            System.out.println("No program found to open: " + fileName);
        }
    }

    /** Чи вміє цей обробник відкривати таке розширення. */
    protected abstract boolean canHandle(String extension);

    /** Власне "відкриття" файлу відповідною програмою. */
    protected abstract void launch(String fileName);

    /** Витягти розширення (у нижньому регістрі). */
    protected String extension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot < 0) ? "" : fileName.substring(dot + 1).toLowerCase();
    }
}
