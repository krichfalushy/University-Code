/**
 * Контекст. Конкретна операція визначається підставленою стратегією.
 */
public class Calculator {

    private Operation operation;

    public Calculator() {}

    public Calculator(Operation operation) {
        this.operation = operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int calculate(int a, int b) {
        if (operation == null) {
            throw new IllegalStateException("Operation is not selected!");
        }
        int res = operation.apply(a, b);
        System.out.printf("%s %s %s = %s%n", a, operation.symbol(), b, res);
        return res;
    }
}
