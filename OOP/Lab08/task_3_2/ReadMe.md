# task_3_2 — Адаптер Автомобільного калькулятора для митниці

Адаптуємо готову бібліотеку VehicleCalculatorLib.jar (інтерфейс VehicleCalculator,
результат — рядок у доларах, напр. "570USD") під інтерфейс української митниці
(Customs: vehiclePrice/tax, гривні).

`CustomsAdapter implements Customs` (у пакеті com.customs, бо клас Auto package-private):
- перетворює Auto -> Vehicle;
- обирає CarCalculator / TruckCalculator;
- парсить рядок "...USD" і конвертує долари -> гривні за курсом;
- додає розрахунок мита (tax) і повну вартість після розмитнення (fullPrice).

Запуск:
`javac -cp libs/VehicleCalculatorLib.jar src/com/customs/*.java`
`java -Dstdout.encoding=UTF-8 -cp "src:libs/VehicleCalculatorLib.jar" com.customs.Main`
