import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        // Підготуємо тестовий файл
        Path file = Files.createTempFile("singleton_demo", ".txt");
        Files.writeString(file,
                "Design patterns in Java\n" +
                "Singleton pattern demo\n" +
                "Reading and processing text files\n");

        // Доступ до Одинака
        FileProcessor fp = FileProcessor.getInstance();

        System.out.println("=== Обробка файлу ===");
        System.out.println("Рядків:  " + fp.countLines(file));
        System.out.println("Слів:    " + fp.countWords(file));
        System.out.println("Символів:" + fp.countChars(file));
        System.out.println("\nВЕРХНІЙ РЕГІСТР:\n" + fp.toUpperCase(file));

        // Перевірка, що екземпляр єдиний
        FileProcessor fp2 = FileProcessor.getInstance();
        System.out.println("Один і той самий екземпляр? " + (fp == fp2));

        Files.deleteIfExists(file);
    }
}
