# task_3_2 — Графічний інтерфейс (Bridge)

Рефакторинг із застосуванням шаблону **Міст**.
До рефакторингу було 9 класів (тип кнопки × розмір).

Розділено на дві ієрархії:
- Абстракція: `Button` -> `RadioButton`, `CheckboxButton`, `DropdownButton`.
- Реалізатор: `Size` -> `SmallSize`, `MediumSize`, `LargeSize`.
`Button` містить `Size` — це міст.

Розширення (без правок наявних класів):
- `ImageButton` — нова абстракція (графічна кнопка).
- `UserSize` — новий реалізатор (користувацький розмір ширина×висота).

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Canvas`
