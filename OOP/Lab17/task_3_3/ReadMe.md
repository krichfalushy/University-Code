# task_3_3 — Обхід Компонувальника (Iterator)

Композит: `Component` -> `Box` (контейнер) / `Item` (лист, name+value).
Два ітератори обходу дерева:
- `DepthFirstIterator` — пошук у глибину (стек);
- `BreadthFirstIterator` — пошук у ширину (черга);
- `CompositeTraversal` — зручні Iterable-обгортки для for-each.

За допомогою ітератора знайдено найдешевший та найдорожчий Item у колекції.
(Component/Box були відсутні у вихідному проєкті — реалізовані тут.)

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
