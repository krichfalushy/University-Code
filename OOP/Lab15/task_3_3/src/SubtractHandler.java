public class SubtractHandler extends OperationHandler {
    @Override protected char operation() { return '-'; }
    @Override protected double calculate(double a, double b) { return a - b; }
}
