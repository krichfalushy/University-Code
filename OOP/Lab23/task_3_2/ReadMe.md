# task_3_2 — Військова база (Visitor)

- `MilitaryObject` — елемент (accept); `GeneralStaff`, `MilitaryBase`.
- `Spy` — відвідувач (visit для кожного типу об'єкта).
- Шпигуни: `SecretAgent` (збір/викрадення секретної інформації),
  `Saboteur` (знищення документації, особового складу та техніки).

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
