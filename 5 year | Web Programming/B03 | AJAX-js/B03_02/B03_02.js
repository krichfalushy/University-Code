// ============================================
// [B]03.02. Зробіть три AJAX-запити з API
//    https://jsonplaceholder.typicode.com/posts/{number},
//    які виконуються послідовно одне за одним.
//    Виведіть їх результати на сторінці у правильному
//    порядку.
//    # Використайте функцію delay(ms), щоб уповільнити
//    виконання AJAX-запитів.
// ============================================


function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function fetchPostSequentially(postId) {
  try {
    const response = await fetch('https://jsonplaceholder.typicode.com/posts/' + postId);
    if (!response.ok) {
      throw new Error('HTTP помилка! Статус: ' + response.status);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    throw new Error('Не вдалося завантажити пост' + postId + ': ' + error.message);
  }
}

async function runSequentialRequests() {
  console.log("\n\n=== Завдання [B]03.02 ===");
  console.log("Виконання трьох AJAX-запитів послідовно...\n");

  try {
    console.log("Запит 1: Завантаження поста #1...");
    const post1 = await fetchPostSequentially(1);
    await delay(500);
    console.log("> Пост 1:", post1);

    console.log("\n Запит 2: Завантаження поста #2...");
    const post2 = await fetchPostSequentially(2);
    await delay(500);
    console.log("> Пост 2:", post2);

    console.log("\n Запит 3: Завантаження поста #3...");
    const post3 = await fetchPostSequentially(3);
    await delay(500);
    console.log("> Пост 3:", post3);

    console.log("\n Всі три запити виконані успішно!");
  } catch (error) {
    console.error("Помилка під час виконання запитів:", error.message);
  }
}

setTimeout(() => {
  runSequentialRequests();
}, 3000);
