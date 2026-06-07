import java.util.ArrayList;
import java.util.List;

/** Квадратне рівняння a*x^2 + b*x + c = 0. coeffs = [a, b, c]. */
public class QuadraticEquation extends Equation {

    public QuadraticEquation(double... coeffs) {
        super(coeffs);
    }

    @Override
    public List<Double> solve() {
        double a = coeffs[0];
        double b = coeffs[1];
        double c = coeffs[2];

        // Якщо a == 0 — вироджується в лінійне b*x + c = 0
        if (a == 0) {
            return new Equation(b, c).solve();
        }

        List<Double> roots = new ArrayList<>();
        double d = b * b - 4 * a * c;          // дискримінант

        if (d < 0) {
            return roots;                       // дійсних розв'язків немає
        } else if (d == 0) {
            roots.add(-b / (2 * a));            // один (подвійний) корінь
        } else {
            double sqrtD = Math.sqrt(d);
            roots.add((-b - sqrtD) / (2 * a));
            roots.add((-b + sqrtD) / (2 * a));  // два корені
        }
        return roots;
    }
}
