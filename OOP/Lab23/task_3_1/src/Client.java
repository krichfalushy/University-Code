public class Client {
    public static void main(String[] args) {
        StaffList staffList = new StaffList();

        staffList.addEmployee(new Manager(60000));
        staffList.addEmployee(new SalesPerson(50000));
        staffList.addEmployee(new SalesPerson(40000));
        staffList.addEmployee(new ITSupport(45000));   // новий тип співробітника

        System.out.println("Total before: " + staffList.getSalary());

        // Підвищення зарплати всім на 10%
        staffList.accept(new RaiseSalaryVisitor(10));
        System.out.println("Total after +10% raise: " + staffList.getSalary());

        // Штраф 5000 кожному
        staffList.accept(new FineVisitor(5000));
        System.out.println("Total after fine 5000: " + staffList.getSalary());
    }
}
