# task_3_2 — Найменший трикутник (Memento + Command)

Зв'язка Знімок + Команда. Додано операції duplicate/half (зміна сторони вдвічі).
- `Triangle` (originator) + `Memento` (порожній інтерфейс).
- `Command` -> `TriangleCommand` (перед зміною робить знімок, undo() відновлює),
  конкретні: SetCommand, DuplicateCommand, HalfCommand.
- Опікун зберігає історію знімків; відновлюється стан із НАЙМЕНШОЮ площею.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
