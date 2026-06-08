/**
 * Відвідувач: підвищує зарплату на заданий відсоток.
 * Можна задати різний відсоток для різних типів співробітників.
 */
public class RaiseSalaryVisitor implements EmployeeVisitor {
    private final double percent;

    public RaiseSalaryVisitor(double percent) {
        this.percent = percent;
    }

    @Override public void visit(Manager manager) {
        manager.setSalary(raise(manager.getSalary()));
    }
    @Override public void visit(SalesPerson salesPerson) {
        salesPerson.setSalary(raise(salesPerson.getSalary()));
    }
    @Override public void visit(ITSupport itSupport) {
        itSupport.setSalary(raise(itSupport.getSalary()));
    }

    private int raise(int salary) {
        return (int) Math.round(salary * (1 + percent / 100.0));
    }
}
