# task_3_1 — Всі працівники (Iterator)

- `StaffList implements Iterable<Employee>` — колекцію можна перебирати у for-each.
- `StaffListIterator implements Iterator<Employee>` — повертає працівників в
  алфавітному порядку імен (сортує копію, не змінюючи оригінал).

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
