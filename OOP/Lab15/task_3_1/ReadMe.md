# task_3_1 — Аутентифікація користувачів (Chain of Responsibility)

Рефакторинг if-else у ланцюг обов'язків.
- `AuthHandler` — базовий обробник (linkWith, handle, passToNext).
- `GuestHandler`, `UserHandler`, `AdminHandler` — обробляють свій тип доступу
  або передають далі. Адмінський пароль додатково має бути >= 8 символів.
- Ланцюг: Guest -> User -> Admin.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
