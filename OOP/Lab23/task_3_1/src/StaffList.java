import java.util.ArrayList;
import java.util.List;

/**
 * Список співробітників. Обходить усіх і застосовує відвідувача до кожного.
 */
public class StaffList implements Employee {

    private final List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    @Override
    public int getSalary() {
        int sum = 0;
        for (Employee e : employees) {
            sum += e.getSalary();
        }
        return sum;
    }

    /** Застосувати відвідувача до всіх співробітників. */
    @Override
    public void accept(EmployeeVisitor visitor) {
        for (Employee e : employees) {
            e.accept(visitor);
        }
    }
}
