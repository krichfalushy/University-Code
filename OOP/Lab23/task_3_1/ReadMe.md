# task_3_1 — Підприємство (Visitor)

- `Employee` — елемент (метод accept); `Manager`, `SalesPerson`, `ITSupport` (новий тип).
- `EmployeeVisitor` — відвідувач (visit для кожного типу).
- Відвідувачі: `RaiseSalaryVisitor` (підвищення на %), `FineVisitor` (штраф).
- `StaffList` обходить усіх співробітників і застосовує відвідувача.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
