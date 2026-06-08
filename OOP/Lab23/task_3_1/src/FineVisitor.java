/**
 * Відвідувач: накладає штраф (фіксована сума) на співробітників.
 * Зарплата не може стати від'ємною.
 */
public class FineVisitor implements EmployeeVisitor {
    private final int fine;

    public FineVisitor(int fine) {
        this.fine = fine;
    }

    @Override public void visit(Manager manager) {
        manager.setSalary(applyFine(manager.getSalary()));
    }
    @Override public void visit(SalesPerson salesPerson) {
        salesPerson.setSalary(applyFine(salesPerson.getSalary()));
    }
    @Override public void visit(ITSupport itSupport) {
        itSupport.setSalary(applyFine(itSupport.getSalary()));
    }

    private int applyFine(int salary) {
        return Math.max(0, salary - fine);
    }
}
