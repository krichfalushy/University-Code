// ============================================
// [B]03.03. Напишіть програму для отримання мемів з API
//    https://api.imgflip.com/get_memes
//    Обробіть відповідь із зазначеною структурою та
//    оберіть випадковий мем через Math.floor(Math.random() * array.length).
//    Відобразіть отримане зображення на сторінці.
//    Застосуйте функцію setTimeout() для того,
//    щоб динамічно оновлювати його кожні 10 секунд.
// ============================================

let currentMemes = [];
let memeDisplayCount = 0;
let timeLeft = 10;

async function fetchMemes() {
    try {
        const response = await fetch('https://api.imgflip.com/get_memes');
        
        if (!response.ok) {
            throw new Error('HTTP помилка! Статус: ' + response.status);
        }

        const data = await response.json();
        
        if (data.success && data.data && data.data.memes) {
            currentMemes = data.data.memes;
            console.log('Завантажено ' + currentMemes.length + ' мемів з API\n');
            displayRandomMeme();
        } else {
            throw new Error('Невірна структура відповіді API');
        }
    } catch (error) {
        console.error('Помилка завантаження: ' + error.message);
    }
}

function displayRandomMeme() {
    if (currentMemes.length === 0) {
        console.log('Меми не знайдено');
        return;
    }

    const randomIndex = Math.floor(Math.random() * currentMemes.length);
    const randomMeme = currentMemes[randomIndex];

    memeDisplayCount++;
    
    console.log('\n' + '='.repeat(60));
    console.log('МЕМ 🤡#' + memeDisplayCount);
    console.log('='.repeat(60));
    console.log('Назва: ' + randomMeme.name);
    console.log('URL: ' + randomMeme.url);
    console.log('Розмір: ' + randomMeme.width + 'x' + randomMeme.height);
    console.log('='.repeat(60) + '\n');
}

// Таймер зворотного відліку
function updateTimer() {
    if (timeLeft === 0) {
        timeLeft = 10;
    } else {
        timeLeft--;
    }
    process.stdout.write('\rНаступне оновлення через: ' + timeLeft + ' сек  ');
}

async function init() {
    console.log('Запуск програми переглядача мемів...\n');
    
    await fetchMemes();
    
    setInterval(() => {
        displayRandomMeme();
        timeLeft = 10;
    }, 10000);

    setInterval(updateTimer, 1000);
}

init();

process.on('SIGINT', () => {
    console.log('\n\nПрограму завершено. Показано мемів: ' + memeDisplayCount);
    process.exit(0);
});