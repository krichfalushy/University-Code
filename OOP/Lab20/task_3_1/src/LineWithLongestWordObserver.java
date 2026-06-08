/** Знаходить рядок, що містить найдовше слово файлу. */
public class LineWithLongestWordObserver implements LineObserver {
    private String bestLine = "";
    private int maxWordLen = -1;

    @Override public void onLine(String line) {
        for (String word : LongestWordObserver.splitWords(line)) {
            if (word.length() > maxWordLen) {
                maxWordLen = word.length();
                bestLine = line;
            }
        }
    }

    @Override public void onComplete() {
        System.out.println("Рядок із найдовшим словом (довжина слова " + maxWordLen + "): \"" + bestLine + "\"");
    }
}
