/**
 * Базовий обробник ланцюга для калькулятора.
 * Кожен обробник виконує "свою" операцію або передає запит далі.
 */
public abstract class OperationHandler {

    private OperationHandler next;

    public OperationHandler linkWith(OperationHandler next) {
        this.next = next;
        return next;
    }

    /** Обробити запит; повертає результат операції. */
    public double handle(OperationRequest request) {
        if (request.getOperation() == operation()) {
            return calculate(request.getLeft(), request.getRight());
        } else if (next != null) {
            return next.handle(request);
        } else {
            throw new UnsupportedOperationException(
                    "Unknown operation: " + request.getOperation());
        }
    }

    /** Символ операції, яку обробляє цей обробник. */
    protected abstract char operation();

    /** Власне обчислення. */
    protected abstract double calculate(double a, double b);
}
