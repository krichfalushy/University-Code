Object.prototype.size = function() {
    let count = 0;
    for (let key in this) {
        if (this.hasOwnProperty(key)) {
            count++;
        }
    }
    return count;
};

function displayResult(title, obj, result) {
    const resultsDiv = document.getElementById('results');
    const resultDiv = document.createElement('div');
    resultDiv.className = 'test-result';
    resultDiv.innerHTML = `
        <strong>${title}</strong>
        <span class="info">Об'єкт:</span> <obj>${JSON.stringify(obj)}</obj><br>
        <span class="success">Результат size():</span> ${result}
    `;
    resultsDiv.appendChild(resultDiv);
}

const obj1 = { name: 'Іван', age: 25, city: 'Київ' };
const obj2 = { a: 1, b: 2 };
const obj3 = {};
const obj4 = { x: 10, y: 20, z: 30, color: 'red', width: 'large' };

function Person(name, age) {
    this.name = name;
    this.age = age;
}
Person.prototype.greet = function() {
    return `Привіт, я ` + this.name;
};
const person1 = new Person('Марія', 30);

displayResult(
'Тест 1: Об\'єкт з 3 полями', obj1, obj1.size()
);
displayResult('Тест 2: Об\'єкт з 2 полями', obj2, obj2.size());
displayResult('Тест 3: Порожній об\'єкт', obj3, obj3.size());
displayResult('Тест 4: Об\'єкт з 5 полями', obj4, obj4.size());
displayResult('Тест 5: Об\'єкт з прототипом (рахуються тільки власні поля)', person1, person1.size());

console.log('=== Детальне тестування ===');
console.log('obj1.size():', obj1.size());
console.log('obj2.size():', obj2.size());
console.log('obj3.size():', obj3.size());
console.log('obj4.size():', obj4.size());
console.log('person1.size():', person1.size(), '(метод greet у прототипі не враховується)');
