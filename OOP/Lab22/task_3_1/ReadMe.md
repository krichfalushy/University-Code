# task_3_1 — Платіжна система (Strategy)

Рефакторинг: спосіб оплати визначається підставленою стратегією.
- `PaymentStrategy` -> `BankAccountPayment`, `PayPalPayment`, `GooglePayPayment`.
- `Customer` (контекст) тримає стратегію, яку можна змінювати під час виконання.
Нові методи оплати додаються без зміни наявного коду.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
