# task_3_1 — Аркада (Template Method)

Рефакторинг: однаковий за структурою алгоритм захисту у Pirate і Troll
винесено в базовий клас.
- `GameObject` — абстрактний базовий клас із ШАБЛОННИМ МЕТОДОМ
  defendAgainstAttack() (final), що задає скелет: pickUpWeapon -> defenseAction
  -> moveToSafety. Самі кроки — абстрактні.
- `Pirate`, `Troll` — реалізують кроки.
- `Knight` — новий персонаж, доданий без зміни алгоритму (лише визначення кроків).

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
