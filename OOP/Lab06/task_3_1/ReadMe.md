# task_3_1 — Прототип для ієрархії рівнянь

Шаблон **Прототип** для `Equation` та нащадків (`QuadraticEquation`, `BiQuadraticEquation`).
- Базовий `Equation implements Cloneable`; `clone()` робить копію та окремо клонує масив
  коефіцієнтів (щоб клон не ділив стан з оригіналом).
- Нащадки перевизначають `clone()` з коваріантним типом повернення.
- `Main` демонструє: клон — інший об'єкт того ж класу; зміна клона не зачіпає оригінал.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
