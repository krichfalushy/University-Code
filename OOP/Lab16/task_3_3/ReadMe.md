# task_3_3 — Пульт керування пристроями (Command)

- `Device` -> `Television`, `Radio`.
- `Command` -> OnCommand, OffCommand, VolumeUp/Down, Next/PrevChanel.
- `MacroCommand` — вимкнути всі пристрої.
- `RemoteController` — інвокер; setDevice() швидко налаштовує під пристрій.
- Універсальний пульт вимикає всі пристрої системи.

Примітка: у вихідному Television логіку volume/channel було інвертовано
(if (isOn) return), через що вони не діяли при ввімкненому ТВ. Виправлено
на if (!isOn) return.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src TVSimulator`
