/**
 * Контекст. Спосіб оплати визначається підставленою стратегією,
 * яку можна змінювати під час виконання.
 */
public class Customer {

    private PaymentStrategy paymentStrategy;

    public Customer() {}

    public Customer(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void makePayment(int amount) {
        if (paymentStrategy == null) {
            System.out.println("Payment method is not selected!");
            return;
        }
        paymentStrategy.pay(amount);
    }
}
