# task_3_2 — Розширений месенджер (Mediator)

Чат на основі посередника + розсилка окремим групам користувачів.
- `ChatMediator` / `ConcreteChatMediator` — посередник: маршрутизує повідомлення
  (sendToAll, sendToUser, sendToGroup).
- `User` / `ConcreteUser` — колеги; кожен має роль (UserRole: ADMIN/MODERATOR/MEMBER)
  і спілкується лише через посередника.
- Додано sendMessageToGroup(message, group) — розсилка адмінам/модераторам тощо.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Client`
