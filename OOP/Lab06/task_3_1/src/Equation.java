import java.util.ArrayList;
import java.util.List;

/**
 * Лінійне рівняння b*x + c = 0. Базовий клас ієрархії.
 * Реалізує шаблон Прототип: метод clone() створює копію рівняння.
 */
public class Equation implements Cloneable {

    public static final int INFINITE_SOLUTIONS = Integer.MAX_VALUE;

    protected double[] coeffs;   // коефіцієнти від старшого степеня

    public Equation(double... coeffs) {
        this.coeffs = coeffs;
    }

    public List<Double> solve() {
        double b = coeffs[0];
        double c = coeffs[1];
        List<Double> roots = new ArrayList<>();
        if (b == 0) {
            if (c == 0) return null;   // нескінченно багато
            return roots;              // немає розв'язків
        }
        roots.add(-c / b);
        return roots;
    }

    public int solutionsCount() {
        List<Double> roots = solve();
        return (roots == null) ? INFINITE_SOLUTIONS : roots.size();
    }

    public double[] getCoeffs() { return coeffs; }

    /**
     * Прототип: створює копію рівняння.
     * Масив коефіцієнтів копіюється (глибока копія змінного стану).
     */
    @Override
    public Equation clone() {
        try {
            Equation copy = (Equation) super.clone();
            copy.coeffs = this.coeffs.clone();   // інакше клон ділив би масив з оригіналом
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);         // не станеться: ми Cloneable
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double c : coeffs) sb.append(String.format("%.0f ", c));
        return getClass().getSimpleName() + "[" + sb.toString().trim() + "]";
    }
}
