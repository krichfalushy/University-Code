/** Відвідувач співробітників. По методу на кожен конкретний тип. */
public interface EmployeeVisitor {
    void visit(Manager manager);
    void visit(SalesPerson salesPerson);
    void visit(ITSupport itSupport);
}
