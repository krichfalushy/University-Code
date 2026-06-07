public class Main {
    public static void main(String[] args) {
        StringBuilderEx sb = new StringBuilderEx();

        // Додавання в кінець
        sb.append("Hello").append(' ').append("world");
        System.out.println("Після append: \"" + sb.build() + "\"");

        // Вставка у вказану позицію
        sb.insert(5, ",");                 // після "Hello"
        System.out.println("Після insert(5, \",\"): \"" + sb.build() + "\"");

        sb.insert(0, ">> ");               // на початок
        System.out.println("Після insert(0, \">> \"): \"" + sb.build() + "\"");

        sb.append('!');                    // знову в кінець
        System.out.println("Фінал: \"" + sb.build() + "\"  (довжина " + sb.length() + ")");

        // Перевірка коректності
        String expected = ">> Hello, world!";
        System.out.println("Очікувано: \"" + expected + "\" -> " +
                (sb.build().equals(expected) ? "OK" : "FAIL"));

        // Перевірка меж
        try {
            sb.insert(1000, "x");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Контроль меж: " + e.getMessage());
        }
    }
}
