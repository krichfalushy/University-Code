import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Колекція працівників. Реалізує Iterable, тому її можна перебирати
 * у циклі for-each. Ітератор повертає працівників за алфавітом імен.
 */
public class StaffList implements Iterable<Employee> {

    private final List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    @Override
    public Iterator<Employee> iterator() {
        return new StaffListIterator(employees);
    }
}
