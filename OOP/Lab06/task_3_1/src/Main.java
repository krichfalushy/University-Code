import java.util.List;

public class Main {
    public static void main(String[] args) {
        Equation linear    = new Equation(2, -8);                 // 2x - 8 = 0
        Equation quadratic = new QuadraticEquation(1, -3, 2);     // x^2 - 3x + 2 = 0
        Equation biquad    = new BiQuadraticEquation(1, 0, -5, 0, 4); // x^4 - 5x^2 + 4 = 0

        Equation[] originals = { linear, quadratic, biquad };

        for (Equation e : originals) {
            Equation copy = e.clone();   // прототип

            System.out.println("Оригінал: " + e + " -> розв'язки: " + fmt(e.solve()));
            System.out.println("Клон:     " + copy + " -> розв'язки: " + fmt(copy.solve()));
            System.out.println("Той самий об'єкт? " + (e == copy)
                    + "; той самий клас? " + (e.getClass() == copy.getClass()));

            // Доводимо незалежність: міняємо коефіцієнти клона
            copy.getCoeffs()[0] = 99;
            System.out.println("Після зміни клона -> оригінал: " + e + ", клон: " + copy);
            System.out.println();
        }
    }

    private static String fmt(List<Double> roots) {
        return (roots == null) ? "нескінченно багато" : roots.toString();
    }
}
