# task_3_2 — Калькулятор (Strategy)

Конкретна операція визначається підставленою стратегією.
- `Operation` -> `Addition`, `Subtraction`, `Multiplication`.
- `Calculator` (контекст) виконує calculate(a, b) через поточну операцію.
Нові операції (ділення тощо) додаються без зміни калькулятора.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
