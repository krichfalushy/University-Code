import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Суб'єкт (Subject/Observable). Порядково читає файл і сповіщає
 * усіх зареєстрованих спостерігачів про кожен прочитаний рядок,
 * а наприкінці — про завершення.
 */
public class FileReader {

    private final String filePath;
    private final List<LineObserver> observers = new ArrayList<>();
    private final Thread thread;

    public FileReader(String filePath) {
        this.filePath = filePath;
        this.thread = new Thread(this::read);
    }

    /** Підписати спостерігача. */
    public void addObserver(LineObserver observer) {
        observers.add(observer);
    }

    /** Відписати спостерігача. */
    public void removeObserver(LineObserver observer) {
        observers.remove(observer);
    }

    /** Запустити читання (в окремому потоці). */
    public void start() {
        thread.start();
    }

    /** Дочекатися завершення читання. */
    public void join() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void read() {
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filePath))) {
            reader.lines().forEach(this::onLineRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyComplete();
    }

    private void onLineRead(String line) {
        for (LineObserver observer : observers) {
            observer.onLine(line);
        }
    }

    private void notifyComplete() {
        for (LineObserver observer : observers) {
            observer.onComplete();
        }
    }
}
