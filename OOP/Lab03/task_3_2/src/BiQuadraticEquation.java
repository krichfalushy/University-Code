import java.util.ArrayList;
import java.util.List;

/**
 * Біквадратне рівняння a*x^4 + b*x^2 + c = 0. coeffs = [a, 0, b, 0, c].
 * Заміна t = x^2 зводить до квадратного a*t^2 + b*t + c = 0,
 * далі для кожного невід'ємного t беремо ±sqrt(t).
 */
public class BiQuadraticEquation extends Equation {

    public BiQuadraticEquation(double... coeffs) {
        super(coeffs);
    }

    @Override
    public List<Double> solve() {
        double a = coeffs[0];
        double b = coeffs[2];
        double c = coeffs[4];

        // Якщо a == 0 — вироджується в b*x^2 + c = 0 (квадратне без лінійного члена)
        if (a == 0) {
            return new QuadraticEquation(b, 0, c).solve();
        }

        // Розв'язуємо a*t^2 + b*t + c = 0 відносно t = x^2
        List<Double> tRoots = new QuadraticEquation(a, b, c).solve();
        if (tRoots == null) {
            return null;
        }

        List<Double> roots = new ArrayList<>();
        for (double t : tRoots) {
            if (t > 0) {
                double x = Math.sqrt(t);
                roots.add(-x);
                roots.add(x);
            } else if (t == 0) {
                roots.add(0.0);          // x = 0 (один корінь)
            }
            // t < 0 -> цей t не дає дійсних x
        }
        return roots;
    }
}
