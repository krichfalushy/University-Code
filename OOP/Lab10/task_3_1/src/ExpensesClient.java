public class ExpensesClient {

    public static void main(String[] args) {
        // Супермаркет очолює директор (менеджер)
        Manager director = new Manager("Director Ivan", 500);
        Department supermarket = new Department("Supermarket", director);

        // --- М'ясний відділ ---
        Department meat = new Department("Meat", new Manager("Mgr Olha", 200));
        meat.add(new Salesperson("Bob", 120));
        meat.add(new Salesperson("Sue", 110));
        meat.add(new Salesperson("Max", 115));

        // --- Відділ молокопродуктів ---
        Department dairy = new Department("Dairy", new Manager("Mgr Petro", 200));
        dairy.add(new Salesperson("Anna", 105));
        dairy.add(new Salesperson("Ihor", 100));
        dairy.add(new Salesperson("Lena", 108));

        // --- Кондитерський відділ ---
        Department pastry = new Department("Pastry", new Manager("Mgr Maria", 200));
        pastry.add(new Salesperson("Yana", 112));
        pastry.add(new Salesperson("Oleg", 118));
        pastry.add(new Salesperson("Nina", 109));

        // Відділи входять до супермаркету
        supermarket.add(meat);
        supermarket.add(dairy);
        supermarket.add(pastry);

        // Клієнт працює однаково з листом, відділом і всім супермаркетом
        System.out.println("Expenses have been requested");
        supermarket.payExpenses();
        System.out.println("\nTotal payroll: $" + supermarket.totalSalary());
        System.out.println("Expenses have been paid");
    }
}
