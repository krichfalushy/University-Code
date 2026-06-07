/** Лист (Leaf) — продавець. Не має підлеглих. */
public class Salesperson implements Employee {

    private final String name;
    private final int salary;

    public Salesperson(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override
    public void payExpenses() {
        System.out.println(name + " (salesperson) has been paid $" + salary);
    }

    @Override
    public int totalSalary() {
        return salary;
    }
}
