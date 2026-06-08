public class Client {
    public static void main(String[] args) {

        // Ланцюг: + -> - -> * -> /
        OperationHandler calculator = new AddHandler();
        calculator.linkWith(new SubtractHandler())
                  .linkWith(new MultiplyHandler())
                  .linkWith(new DivideHandler());

        OperationRequest[] requests = {
            new OperationRequest(7, '+', 3),
            new OperationRequest(10, '-', 4),
            new OperationRequest(6, '*', 5),
            new OperationRequest(20, '/', 4),
            new OperationRequest(8, '/', 0),    // ділення на нуль
            new OperationRequest(2, '^', 3),    // невідома операція
        };

        for (OperationRequest req : requests) {
            try {
                double result = calculator.handle(req);
                System.out.println(req + " = " + result);
            } catch (RuntimeException e) {
                System.out.println(req + " -> Error: " + e.getMessage());
            }
        }
    }
}
