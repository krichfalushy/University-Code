public class Client {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        calculator.setOperation(new Subtraction());
        calculator.calculate(12, 13);

        calculator.setOperation(new Addition());
        calculator.calculate(7, 5);

        calculator.setOperation(new Multiplication());
        calculator.calculate(6, 4);
    }
}
