public class MultiplyHandler extends OperationHandler {
    @Override protected char operation() { return '*'; }
    @Override protected double calculate(double a, double b) { return a * b; }
}
