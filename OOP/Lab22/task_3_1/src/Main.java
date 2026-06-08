public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer();

        // Обираємо конкретну стратегію оплати
        customer.setPaymentStrategy(new BankAccountPayment());
        customer.makePayment(100);

        // Змінюємо стратегію під час виконання
        customer.setPaymentStrategy(new PayPalPayment());
        customer.makePayment(250);

        customer.setPaymentStrategy(new GooglePayPayment());
        customer.makePayment(75);
    }
}
