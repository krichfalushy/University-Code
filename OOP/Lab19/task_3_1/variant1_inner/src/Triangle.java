/**
 * ВАРІАНТ 1 — Знімок на основі внутрішніх класів.
 * Memento є внутрішнім (вкладеним) класом Triangle, тому має доступ до його
 * приватних полів, а ззовні стан знімка недоступний.
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

    /** Створити знімок поточного стану. */
    public Memento save() {
        return new Memento(a, b, c);
    }

    /** Відновити стан зі знімка. */
    public void restore(Memento m) {
        this.a = m.a; this.b = m.b; this.c = m.c;
    }

    /**
     * Знімок — внутрішній клас. Його поля приватні; зовнішній код не може
     * прочитати чи змінити стан, але Triangle (зовнішній клас) має доступ.
     */
    public class Memento {
        private final float a, b, c;
        private Memento(float a, float b, float c) {
            this.a = a; this.b = b; this.c = c;
        }
    }
}
