# task_3_2 — Музичний плеєр (State)

- `State` -> `StoppedState`, `PlayingState`, `PausedState`.
- `MediaPlayer` (контекст) делегує play/pause/stop/next/prev поточному стану;
  next/prev перемикають треки по колу.
Кожна дія поводиться відповідно до стану (напр., на Stopped не можна перемикати треки).

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
