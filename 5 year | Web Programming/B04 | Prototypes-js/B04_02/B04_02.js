function Shape() {}

Shape.prototype.area = function() {
    return 0;
};

function Ellipse(a, b) {
    this.a = a;
    this.b = b;
}

Ellipse.prototype = Object.create(Shape.prototype);
Ellipse.prototype.constructor = Ellipse;

Ellipse.prototype.area = function() {
    return Math.PI * this.a * this.b;
};

function Circle(r) {
    this.r = r;
}

Circle.prototype = Object.create(Shape.prototype);
Circle.prototype.constructor = Circle;

Circle.prototype.area = function() {
    return Math.PI * this.r * this.r;
};

function displayShape(name, shape, params) {
    const resultsDiv = document.getElementById('results');
    const shapeDiv = document.createElement('div');
    shapeDiv.className = 'shape-card';

    const area = shape.area();

    shapeDiv.innerHTML = `
        <div class="shape-type">${name}</div>
        <p><strong>Параметри:</strong> ${params}</p>
        <div class="result">
            <strong>Площа:</strong> ${area.toFixed(4)}
        </div>
    `;
    resultsDiv.appendChild(shapeDiv);
}

const shape1 = new Shape();
const ellipse1 = new Ellipse(5, 3);
const ellipse2 = new Ellipse(10, 7);
const circle1 = new Circle(5);
const circle2 = new Circle(10);
const circle3 = new Circle(2.5);

displayShape('Shape (базовий)', shape1, 'немає параметрів');
displayShape('Ellipse', ellipse1, 'a = 5, b = 3');
displayShape('Ellipse', ellipse2, 'a = 10, b = 7');
displayShape('Circle', circle1, 'r = 5');
displayShape('Circle', circle2, 'r = 10');
displayShape('Circle', circle3, 'r = 2.5');
