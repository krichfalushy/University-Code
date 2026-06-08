# task_3_2 — Текстовий редактор (Command)

- `Command` -> `SaveCommand`, `PrintCommand`, `ShowInfoCommand` (одержувач — Document).
- Інвокери: `Button`, `MenuItem`, `Shortcut` — кожен тримає команду й виконує її.
- Одна команда може бути прив'язана до кнопки, пункту меню та шортката одночасно.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
