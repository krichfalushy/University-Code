# task_3_3 — Калькулятор (Chain of Responsibility)

Кожен запит — два числа й тип операції. Ланцюг обробників виконує "свою"
операцію або передає далі.
- `OperationRequest` — запит (left, operation, right).
- `OperationHandler` — базовий обробник.
- AddHandler(+), SubtractHandler(-), MultiplyHandler(*), DivideHandler(/).
- Оброблено ділення на нуль та невідому операцію.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
