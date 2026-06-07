# task_3_1 — Автомобільний завод (Builder)

Шаблон **Будівельник**. Для кожної комплексної складової — власний білдер:
`Engine.Builder`, `Wheel.Builder`, `Transmission.Builder`; головний `Car.Builder`
збирає авто з готових частин. Клас `Director` поєднує процес і має методи для різних
класів авто: економ-седан, преміум-SUV, електро-хетчбек.

- car_builder_uml.png — діаграма класів реалізованого шаблону.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
