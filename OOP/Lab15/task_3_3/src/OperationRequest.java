/** Запит на арифметичну операцію: два операнди та символ операції. */
public class OperationRequest {
    private final double left;
    private final double right;
    private final char operation;

    public OperationRequest(double left, char operation, double right) {
        this.left = left;
        this.operation = operation;
        this.right = right;
    }

    public double getLeft() { return left; }
    public double getRight() { return right; }
    public char getOperation() { return operation; }

    @Override
    public String toString() {
        return left + " " + operation + " " + right;
    }
}
