import java.util.Iterator;

/**
 * Зручні Iterable-обгортки над деревом Компонувальника для обходу
 * в глибину та в ширину.
 */
public class CompositeTraversal {

    public static Iterable<Component> depthFirst(Component root) {
        return () -> new DepthFirstIterator(root);
    }

    public static Iterable<Component> breadthFirst(Component root) {
        return () -> new BreadthFirstIterator(root);
    }
}
