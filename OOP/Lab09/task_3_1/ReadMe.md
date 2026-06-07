# task_3_1 — Кав'ярня (Bridge)

Рефакторинг із застосуванням шаблону **Міст**.
До рефакторингу було 6 класів (тип напою × спосіб: Black/Milk).

Розділено на дві незалежні ієрархії:
- Абстракція: `Beverage` -> `Coffee`, `Tee`, `Chocolate` (тип напою).
- Реалізатор: `Preparation` -> `BlackPreparation`, `MilkPreparation` (наповнювач).
`Beverage` містить `Preparation` — це міст.

Додано вимір подачі `Serving` (DINE_IN / TAKEAWAY) — приготування "в ресторані"
та "на винос" без породження нових класів (комбінується з будь-яким напоєм і способом).

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Cafe`
