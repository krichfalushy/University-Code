/* Задача 02.01
  Повертає true, якщо аргумент рядок або об'єкт String
*/

function isString(obj) {
  return typeof obj === 'string' || obj instanceof String;
}

// Перевірка
console.log(isString("Hello"));
console.log(isString(new String("World")));
console.log(isString(123)); // false