# task_3_2 — Відкриття файлів (Chain of Responsibility)

Тип файлу визначається розширенням; ланцюг обробників відкриває файл
відповідною програмою або передає далі.
- `FileHandler` — базовий обробник.
- ImageHandler, WordHandler, ExcelHandler, PowerPointHandler, PdfHandler.
- Невідоме розширення -> жоден обробник не спрацьовує (повідомлення).

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
