# task_3_2 — Магазин (Iterator)

`Shop` зберігає товари у HashMap і надає два ітератори:
- `byName()` — за алфавітним порядком назв;
- `byQuantityDesc()` — за спаданням кількості.
Реалізовано через окремі Iterable-обгортки над відсортованою копією.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
