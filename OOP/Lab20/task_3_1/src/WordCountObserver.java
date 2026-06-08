/** Підраховує загальну кількість слів у файлі. */
public class WordCountObserver implements LineObserver {
    private int count = 0;

    @Override public void onLine(String line) {
        count += LongestWordObserver.splitWords(line).length;
    }

    @Override public void onComplete() {
        System.out.println("Кількість слів у файлі: " + count);
    }
}
