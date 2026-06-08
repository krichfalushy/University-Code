import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Ітератор для StaffList. Перебирає працівників в алфавітному порядку імен,
 * не змінюючи вихідну колекцію (сортуємо копію).
 */
public class StaffListIterator implements Iterator<Employee> {

    private final List<Employee> sorted;
    private int index = 0;

    public StaffListIterator(List<Employee> employees) {
        this.sorted = new ArrayList<>(employees);
        this.sorted.sort(Comparator.comparing(Employee::getName));
    }

    @Override
    public boolean hasNext() {
        return index < sorted.size();
    }

    @Override
    public Employee next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return sorted.get(index++);
    }
}
