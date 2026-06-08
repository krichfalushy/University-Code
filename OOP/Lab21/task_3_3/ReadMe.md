# task_3_3 — Вентилятор (State)

Рефакторинг switch-по-рядку на шаблон Стан.
- `State` -> `LowState`, `MediumState`, `HighState`.
- `Fan` (контекст) делегує turnUp()/turnDown() поточному стану.
Вивід ідентичний оригінальному.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
