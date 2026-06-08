/** Знаходить найдовший рядок файлу. */
public class LongestLineObserver implements LineObserver {
    private String longest = "";

    @Override public void onLine(String line) {
        if (line.length() > longest.length()) {
            longest = line;
        }
    }

    @Override public void onComplete() {
        System.out.println("Найдовший рядок (" + longest.length() + " символів): \"" + longest + "\"");
    }
}
