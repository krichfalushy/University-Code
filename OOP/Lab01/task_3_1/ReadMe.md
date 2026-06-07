# task_3_1 — Builder

Реалізація структури класів за UML-діаграмою.
Патерн: **Builder** (CarBuilder будує Car; CarSimulator — Director).

- Car — продукт, композиційно володіє Engine та Wheel.
- CarBuilder — покроково створює Car.
- CarSimulator — керує послідовністю побудови, володіє CarBuilder і Wheel.
