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
let autoRefreshInterval;
let countdownInterval;
let timeLeft = 10;
let memeDisplayCount = 0;

const memeContainer = document.getElementById('memeContainer');
const timerElement = document.getElementById('timer');
const refreshBtn = document.getElementById('refreshBtn');
const memeCountElement = document.getElementById('memeCount');

async function fetchMemes() {
    try {
        const response = await fetch('https://api.imgflip.com/get_memes');

        if (!response.ok) {
            throw new Error('HTTP помилка! Статус: ' + response.status);
        }

        const data = await response.json();

        if (data.success && data.data && data.data.memes) {
            currentMemes = data.data.memes;
            displayRandomMeme();
        } else {
            throw new Error('Невірна структура відповіді API');
        }
    } catch (error) {
        displayError('Помилка завантаження: ' + error.message);
    }
}

function displayRandomMeme() {
    if (currentMemes.length === 0) {
        memeContainer.innerHTML = '<div class="error">Меми не знайдені</div>';
        return;
    }

    const randomIndex = Math.floor(Math.random() * currentMemes.length);
    const randomMeme = currentMemes[randomIndex];

    memeContainer.innerHTML =
        '<img src="' + randomMeme.url + '" ' +
        'alt="' + randomMeme.name + '" ' +
        'class="meme-image" ' +
        'onerror="this.parentElement.innerHTML=\'<div class=&quot;error&quot;>Помилка завантаження зображення</div>\'">' +
        '<div class="meme-title">' + randomMeme.name + '</div>';

    memeDisplayCount++;
    memeCountElement.textContent = memeDisplayCount;
}

function displayError(message) {
    memeContainer.innerHTML = '<div class="error">' + message + '</div>';
}

function updateTimer() {
    if (timeLeft === 0) {
        timeLeft = 10;
    } else {
        timeLeft--;
    }
    timerElement.textContent = timeLeft;
}

function resetTimer() {
    timeLeft = 10;
    timerElement.textContent = timeLeft;
}

async function init() {
    await fetchMemes();

    autoRefreshInterval = setInterval(() => {
        displayRandomMeme();
        resetTimer();
    }, 10000);

    countdownInterval = setInterval(updateTimer, 1000);
}

refreshBtn.addEventListener('click', () => {
    displayRandomMeme();
    resetTimer();
});

init();