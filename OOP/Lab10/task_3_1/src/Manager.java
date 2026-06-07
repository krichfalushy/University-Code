/** Лист (Leaf) — менеджер як окремий працівник (особиста зарплата). */
public class Manager implements Employee {

    private final String name;
    private final int salary;

    public Manager(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() { return name; }

    @Override
    public void payExpenses() {
        System.out.println(name + " (manager) has been paid $" + salary);
    }

    @Override
    public int totalSalary() {
        return salary;
    }
}
