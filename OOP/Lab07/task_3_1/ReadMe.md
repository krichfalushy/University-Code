# task_3_1 — Одинак для обробки текстових файлів

Шаблон **Одинак (Singleton)**. `FileProcessor` — єдина точка доступу до операцій
читання й обробки текстових файлів: read, readLines, countLines/Words/Chars,
toUpperCase, write.
- Eager-ініціалізація: `private static final FileProcessor instance = new FileProcessor();`
- Приватний конструктор + `getInstance()`.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
