import java.util.ArrayList;
import java.util.List;

/**
 * Композит (Composite) — відділ. Очолюється менеджером і містить
 * співробітників, серед яких можуть бути й інші відділи (рекурсія).
 * Реалізує той самий інтерфейс Employee, що й листи.
 */
public class Department implements Employee {

    private final String title;
    private final Manager head;                       // менеджер, що очолює відділ
    private final List<Employee> members = new ArrayList<>();

    public Department(String title, Manager head) {
        this.title = title;
        this.head = head;
    }

    /** Додати співробітника або підвідділ. */
    public void add(Employee employee) {
        members.add(employee);
    }

    public void remove(Employee employee) {
        members.remove(employee);
    }

    @Override
    public void payExpenses() {
        System.out.println("--- Department '" + title + "' (head: " + head.getName() + ") ---");
        head.payExpenses();                  // спершу платимо менеджеру відділу
        for (Employee member : members) {    // потім усім підлеглим (рекурсивно)
            member.payExpenses();
        }
    }

    @Override
    public int totalSalary() {
        int sum = head.totalSalary();
        for (Employee member : members) {
            sum += member.totalSalary();
        }
        return sum;
    }
}
