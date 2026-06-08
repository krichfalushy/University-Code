/** Оплата через Google Pay (розширення без зміни наявного коду). */
public class GooglePayPayment implements PaymentStrategy {
    @Override public void pay(int amount) {
        System.out.println("Payment of $" + amount + " made from Google Pay.");
    }
}
