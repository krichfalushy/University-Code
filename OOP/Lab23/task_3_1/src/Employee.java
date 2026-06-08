/** Елемент ієрархії. Приймає відвідувача (accept). */
public interface Employee {
    int getSalary();
    void accept(EmployeeVisitor visitor);
}
