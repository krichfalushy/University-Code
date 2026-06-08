/** Оплата через PayPal. */
public class PayPalPayment implements PaymentStrategy {
    @Override public void pay(int amount) {
        System.out.println("Payment of $" + amount + " made from PayPal.");
    }
}
