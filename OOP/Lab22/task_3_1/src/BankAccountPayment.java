/** Оплата з банківського рахунку. */
public class BankAccountPayment implements PaymentStrategy {
    @Override public void pay(int amount) {
        System.out.println("Payment of $" + amount + " made from bank account.");
    }
}
