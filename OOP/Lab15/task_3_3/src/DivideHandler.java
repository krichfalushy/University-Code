public class DivideHandler extends OperationHandler {
    @Override protected char operation() { return '/'; }
    @Override protected double calculate(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }
}
