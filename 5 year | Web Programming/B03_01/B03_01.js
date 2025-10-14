// ============================================
// [B]03.01. Опишіть дві асинхронні функції, кожна з яких
//   кидає виключення з ймовірністю 50%. Викличте їх пара-
//   лельно через Promise.any(). Виведіть повідомлення про
//   успішність виконання програми.
// ============================================

function asyncFunction1() {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const success = Math.random() >= 0.5;
      if (success) {
        resolve("Функція 1 виконалась успішно!");
      } else {
        reject(new Error("Щось пішло не так у функції 1..."));
      }
    }, 1000);
  });
}

function asyncFunction2() {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const success = Math.random() >= 0.5;
      if (success) {
        resolve("Функція 2 виконалась успішно!");
      } else {
        reject(new Error("Щось пішло не так у функції 2..."));
      }
    }, 1500);
  });
}

console.log("=== Завдання [B]03.01 ===");

Promise.any([asyncFunction1(), asyncFunction2()])
  .then(result => {
    console.log("Успіх! Перша успішна функція повернула:", result);
  })
  .catch(error => {
    console.log("Обидві функції викинули помилки:", error);
  });
