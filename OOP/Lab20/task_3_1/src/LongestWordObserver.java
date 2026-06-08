/** Знаходить найдовше слово у файлі. */
public class LongestWordObserver implements LineObserver {
    private String longestWord = "";

    @Override public void onLine(String line) {
        for (String word : splitWords(line)) {
            if (word.length() > longestWord.length()) {
                longestWord = word;
            }
        }
    }

    @Override public void onComplete() {
        System.out.println("Найдовше слово (" + longestWord.length() + " символів): \"" + longestWord + "\"");
    }

    static String[] splitWords(String line) {
        String trimmed = line.trim();
        return trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
    }
}
