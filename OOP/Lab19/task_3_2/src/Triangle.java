/**
 * Трикутник (Originator). Зберігає/відновлює свій стан через Memento.
 * Додано операції duplicate / half (збільшення чи зменшення сторони вдвічі).
 */
public class Triangle {

    private float a, b, c;

    public static boolean checkExistence(float a, float b, float c) {
        return a + b > c && a + c > b && b + c > a;
    }

    public Triangle(float a, float b, float c) {
        assert checkExistence(a, b, c);
        this.a = a; this.b = b; this.c = c;
    }

    public void setA(float a) { if (checkExistence(a, b, c)) this.a = a; else err(); }
    public void setB(float b) { if (checkExistence(a, b, c)) this.b = b; else err(); }
    public void setC(float c) { if (checkExistence(a, b, c)) this.c = c; else err(); }

    public void duplicateA() { if (checkExistence(2*a, b, c)) a *= 2; else err(); }
    public void halfA()      { if (checkExistence(a/2, b, c)) a /= 2; else err(); }
    public void duplicateB() { if (checkExistence(a, 2*b, c)) b *= 2; else err(); }
    public void halfB()      { if (checkExistence(a, b/2, c)) b /= 2; else err(); }
    public void duplicateC() { if (checkExistence(a, b, 2*c)) c *= 2; else err(); }
    public void halfC()      { if (checkExistence(a, b, c/2)) c /= 2; else err(); }

    private void err() { System.out.println("Error: value is not set"); }

    public float perimeter() { return a + b + c; }

    public float square() {
        float p = perimeter() / 2;
        float s = p * (p - a) * (p - b) * (p - c);
        return (float) Math.sqrt(s);
    }

    @Override public String toString() {
        return String.format("Triangle{a=%.2f, b=%.2f, c=%.2f}", a, b, c);
    }

    public Memento save() { return new ConcreteMemento(a, b, c); }

    public void restore(Memento memento) {
        ConcreteMemento m = (ConcreteMemento) memento;
        this.a = m.a; this.b = m.b; this.c = m.c;
    }

    private static class ConcreteMemento implements Memento {
        private final float a, b, c;
        private ConcreteMemento(float a, float b, float c) { this.a = a; this.b = b; this.c = c; }
    }
}
