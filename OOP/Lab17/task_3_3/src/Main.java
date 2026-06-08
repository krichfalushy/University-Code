import java.util.Random;

public class Main {
    public static void main(String[] args) {

        Random random = new Random(42);   // фіксований seed для відтворюваності

        final Box root = new Box(-999);
        for (int j = 0; j < 10; j++) {
            final Box box = new Box(j);
            root.add(box);
            for (int i = 0; i < 10; i++) {
                String name = String.format("Item_%s", j * 10 + i);
                int value = random.nextInt(1000);
                box.add(new Item(name, value));
            }
        }

        // Обхід у глибину: рахуємо кількість вузлів
        int dfsCount = 0;
        for (Component c : CompositeTraversal.depthFirst(root)) {
            dfsCount++;
        }

        // Обхід у ширину
        int bfsCount = 0;
        for (Component c : CompositeTraversal.breadthFirst(root)) {
            bfsCount++;
        }

        System.out.println("DFS visited nodes: " + dfsCount);
        System.out.println("BFS visited nodes: " + bfsCount);

        // Пошук найдешевшого та найдорожчого Item через ітератор (DFS)
        Item cheapest = null;
        Item mostExpensive = null;
        int items = 0;
        for (Component c : CompositeTraversal.depthFirst(root)) {
            if (c instanceof Item) {
                Item item = (Item) c;
                items++;
                if (cheapest == null || item.getValue() < cheapest.getValue()) {
                    cheapest = item;
                }
                if (mostExpensive == null || item.getValue() > mostExpensive.getValue()) {
                    mostExpensive = item;
                }
            }
        }

        System.out.println("\nTotal items: " + items);
        System.out.println("Cheapest item:       " + cheapest);
        System.out.println("Most expensive item: " + mostExpensive);

        // Демонстрація різниці порядку: перші 5 вузлів кожного обходу
        System.out.println("\nFirst 5 nodes DFS:");
        printFirst(CompositeTraversal.depthFirst(root), 5);
        System.out.println("First 5 nodes BFS:");
        printFirst(CompositeTraversal.breadthFirst(root), 5);
    }

    private static void printFirst(Iterable<Component> traversal, int n) {
        int i = 0;
        for (Component c : traversal) {
            if (i++ >= n) break;
            System.out.println("  " + c);
        }
    }
}
