# task_3_2 — Кава з допами (Decorator)

- `Beverage` — компонент; `Espresso`, `DarkRoast`, `Decaf` — конкретні напої.
- `CondimentDecorator` — базовий декоратор наповнювача; конкретні:
  Milk (молоко), Sugar (цукор), Cream (вершки), Whip (крем).

`Main` створює: еспресо з 2 цукрами; чорну каву з вершками та 2 цукрами;
чорну каву з кремом та 1 цукром; декаф з молоком та 2 цукрами.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
