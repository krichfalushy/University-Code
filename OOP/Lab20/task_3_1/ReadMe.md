# task_3_1 — Аналіз текстових файлів (Observer)

`FileReader` — суб'єкт (Subject): порядково читає файл і сповіщає підписаних
спостерігачів про кожен рядок (onLine) та про завершення (onComplete).

Спостерігачі (LineObserver):
- `LongestLineObserver` — найдовший рядок файлу;
- `LongestWordObserver` — найдовше слово;
- `WordCountObserver` — кількість слів;
- `LineWithLongestWordObserver` — рядок, що містить найдовше слово.

Читання виконується в окремому потоці; Main викликає start()+join(),
щоб дочекатися завершення й побачити результати.

Запуск (з кореня проєкту, де лежить resources/):
`javac src/*.java -d src && java -Dstdout.encoding=UTF-8 -cp src Main`
