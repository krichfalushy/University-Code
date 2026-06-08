import java.util.ArrayList;
import java.util.List;

/**
 * Композит — коробка. Може містити інші коробки та елементи.
 * Надає доступ до дочірніх компонентів для побудови ітераторів.
 */
public class Box implements Component {

    private final int id;
    private final List<Component> children = new ArrayList<>();

    public Box(int id) {
        this.id = id;
    }

    public void add(Component component) {
        children.add(component);
    }

    public List<Component> getChildren() {
        return children;
    }

    @Override
    public void execute() {
        for (Component child : children) {
            child.execute();
        }
    }

    @Override
    public String toString() {
        return "Box{id=" + id + ", children=" + children.size() + "}";
    }
}
