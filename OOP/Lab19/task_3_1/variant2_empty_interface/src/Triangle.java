/**
 * ВАРІАНТ 2 — Знімок із порожнім інтерфейсом.
 * Конкретний знімок (приватний вкладений клас) реалізує порожній інтерфейс
 * Memento. Зовні видно лише інтерфейс, тож стан недоступний.
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

    private void err() { System.out.println("Error: value is not set"); }

    public float perimeter() { return a + b + c; }

    public float square() {
        float p = perimeter() / 2;
        float s = p * (p - a) * (p - b) * (p - c);
        return (float) Math.sqrt(s);
    }

    @Override public String toString() {
        return String.format("Triangle{a=%.1f, b=%.1f, c=%.1f}", a, b, c);
    }

    public Memento save() {
        return new ConcreteMemento(a, b, c);
    }

    public void restore(Memento memento) {
        // originator знає реальний тип і дістає з нього стан
        ConcreteMemento m = (ConcreteMemento) memento;
        this.a = m.a; this.b = m.b; this.c = m.c;
    }

    /** Приватна реалізація знімка — невидима зовні. */
    private static class ConcreteMemento implements Memento {
        private final float a, b, c;
        private ConcreteMemento(float a, float b, float c) {
            this.a = a; this.b = b; this.c = c;
        }
    }
}
