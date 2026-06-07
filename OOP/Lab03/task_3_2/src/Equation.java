import java.util.ArrayList;
import java.util.List;

/**
 * Лінійне рівняння виду b*x + c = 0.
 * Базовий клас ієрархії. Коефіцієнти задаються від старшого степеня.
 */
public class Equation {

    /** Спеціальне значення: рівняння має нескінченно багато розв'язків. */
    public static final int INFINITE_SOLUTIONS = Integer.MAX_VALUE;

    protected final double[] coeffs;   // коефіцієнти від старшого степеня

    public Equation(double... coeffs) {
        this.coeffs = coeffs;
    }

    /**
     * Розв'язки лінійного рівняння b*x + c = 0.
     * coeffs = [b, c].
     */
    public List<Double> solve() {
        double b = coeffs[0];
        double c = coeffs[1];
        List<Double> roots = new ArrayList<>();

        if (b == 0) {
            // c = 0  -> нескінченно розв'язків; інакше — розв'язків немає
            if (c == 0) {
                return null;   // null трактуємо як "нескінченно багато"
            }
            return roots;      // порожній список -> розв'язків немає
        }
        roots.add(-c / b);
        return roots;
    }

    /**
     * Кількість розв'язків: число або INFINITE_SOLUTIONS.
     */
    public int solutionsCount() {
        List<Double> roots = solve();
        if (roots == null) {
            return INFINITE_SOLUTIONS;
        }
        return roots.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double c : coeffs) {
            sb.append(String.format("%.0f ", c));
        }
        return "[" + sb.toString().trim() + "]";
    }
}
