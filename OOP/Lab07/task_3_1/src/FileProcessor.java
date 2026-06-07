import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Одинак (Singleton) для читання та обробки текстових файлів.
 * Єдина точка доступу до операцій з файлами в усій програмі.
 */
public class FileProcessor {

    // Eager-ініціалізація: єдиний екземпляр створюється при завантаженні класу.
    private static final FileProcessor instance = new FileProcessor();

    // Приватний конструктор — заборона створення ззовні.
    private FileProcessor() {
    }

    public static FileProcessor getInstance() {
        return instance;
    }

    /** Прочитати весь файл у рядок. */
    public String read(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /** Прочитати файл по рядках. */
    public List<String> readLines(Path path) throws IOException {
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    /** Кількість рядків у файлі. */
    public int countLines(Path path) throws IOException {
        return readLines(path).size();
    }

    /** Кількість слів у файлі. */
    public int countWords(Path path) throws IOException {
        String text = read(path).trim();
        if (text.isEmpty()) return 0;
        return text.split("\\s+").length;
    }

    /** Кількість символів у файлі. */
    public long countChars(Path path) throws IOException {
        return read(path).length();
    }

    /** Перетворити вміст файлу у верхній регістр. */
    public String toUpperCase(Path path) throws IOException {
        return read(path).toUpperCase();
    }

    /** Записати рядок у файл (для збереження обробленого тексту). */
    public void write(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }
}
