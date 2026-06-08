# task_3_1 — Найбільший трикутник (Memento)

Зберігання історії станів трикутника та відновлення стану з НАЙБІЛЬШОЮ площею.
Реалізовано всі три способи шаблону Знімок (кожен — окремий підпроєкт):

- variant1_inner — на основі внутрішніх класів (Memento — внутрішній клас Triangle).
- variant2_empty_interface — з порожнім інтерфейсом Memento (опікун не бачить стану).
- variant3_protected — з підвищеним захистом (окремий клас Memento,
  стан доступний лише в межах пакету через package-private поля).

Запуск (для кожного варіанту):
`javac <variant>/src/*.java && java -Dstdout.encoding=UTF-8 -cp <variant>/src Client`
