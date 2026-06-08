import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.NoSuchElementException;

/**
 * Ітератор обходу дерева Компонувальника в ШИРИНУ (BFS) на основі черги.
 */
public class BreadthFirstIterator implements Iterator<Component> {

    private final Queue<Component> queue = new ArrayDeque<>();

    public BreadthFirstIterator(Component root) {
        queue.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Component next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Component current = queue.poll();
        if (current instanceof Box) {
            queue.addAll(((Box) current).getChildren());
        }
        return current;
    }
}
