# task_3_1 — Декорування рядка (Decorator)

- `Printable` — компонент; `PrintableString` — конкретний компонент.
- `StringDecorator` — базовий декоратор; конкретні:
  PostComaDecorator, PostEndlDecorator, PostSpaceDecorator, PostExclaimDecorator,
  PostWordDecorator (слово в кінець), PreWordDecorator (слово на початок).

`Main` збирає "Hello, World!" з порожнього базового рядка через ланцюг декораторів.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
