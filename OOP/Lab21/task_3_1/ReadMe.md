# task_3_1 — MediaPlayer (State)

Рефакторинг switch-по-рядку на шаблон Стан.
- `State` -> `PlayingState`, `PausedState`.
- `MediaPlayer` (контекст) делегує play()/pause() поточному стану.
Вивід ідентичний оригінальному.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
