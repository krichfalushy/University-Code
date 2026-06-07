# task_3_1 — Coffee shop (Factory Method)

Кав'ярня у ТРЦ MechMat Mall. Реалізовано патерн **Фабричний метод**:
- `Beverage` — абстрактний продукт (собівартість = сума компонентів, ціна продажу).
- `Espresso`, `Americano`, `Cappuccino`, `Latte` — базові напої; `HotChocolate` — розширення асортименту.
- `BeverageFactory` + конкретні фабрики — фабричний метод `createBeverage()`.
- `CoffeeShop` — продає напої та рахує загальний прибуток за період.

Запуск: `javac src/*.java && java -Dstdout.encoding=UTF-8 -cp src Main`
