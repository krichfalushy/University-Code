import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Ітератор обходу дерева Компонувальника в ГЛИБИНУ (DFS) на основі стека.
 * Повертає всі вузли (і Box, і Item).
 */
public class DepthFirstIterator implements Iterator<Component> {

    private final Deque<Component> stack = new ArrayDeque<>();

    public DepthFirstIterator(Component root) {
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public Component next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Component current = stack.pop();
        if (current instanceof Box) {
            List<Component> children = ((Box) current).getChildren();
            // кладемо у зворотному порядку, щоб обходити зліва направо
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }
        return current;
    }
}
