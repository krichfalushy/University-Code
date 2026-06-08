# task_3_1 — Універсальний пульт для світильників (Command)

- `Command` — інтерфейс команди; `LightOnCommand`, `LightOffCommand` — конкретні.
- `MacroCommand` — складена команда ("вимкнути всі").
- `Controller` — інвокер (пульт), що тримає команди on/off.
- Універсальний пульт вимикає всі світильники однією макрокомандою.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
