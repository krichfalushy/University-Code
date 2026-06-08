# task_3_1 — Симулятор польотів (Mediator)

Рефакторинг: літаки більше не керують смугою та списками напряму.
- `ControlTower` — посередник: тримає Runway і списки літаків у польоті/на землі,
  обробляє запити requestTakeOff/requestLanding.
- `Plane` — колега: лише надсилає запити вежі (takeOff/land), не знаючи про смугу
  чи інші літаки.
- `Runway` керується тільки посередником.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
