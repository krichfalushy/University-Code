public class Main {
    public static void main(String[] args) {
        FileReader fileReader = new FileReader("resources/input01.txt");

        // Підписуємо спостерігачів
        fileReader.addObserver(new LongestLineObserver());
        fileReader.addObserver(new LongestWordObserver());
        fileReader.addObserver(new WordCountObserver());
        fileReader.addObserver(new LineWithLongestWordObserver());

        // Запускаємо читання й чекаємо завершення, щоб побачити результати
        fileReader.start();
        fileReader.join();
    }
}
