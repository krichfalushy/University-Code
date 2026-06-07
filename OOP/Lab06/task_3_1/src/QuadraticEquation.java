import java.util.ArrayList;
import java.util.List;

/** Квадратне рівняння a*x^2 + b*x + c = 0. coeffs = [a, b, c]. */
public class QuadraticEquation extends Equation {

    public QuadraticEquation(double... coeffs) {
        super(coeffs);
    }

    @Override
    public List<Double> solve() {
        double a = coeffs[0], b = coeffs[1], c = coeffs[2];
        if (a == 0) return new Equation(b, c).solve();

        List<Double> roots = new ArrayList<>();
        double d = b * b - 4 * a * c;
        if (d < 0) {
            return roots;
        } else if (d == 0) {
            roots.add(-b / (2 * a));
        } else {
            double s = Math.sqrt(d);
            roots.add((-b - s) / (2 * a));
            roots.add((-b + s) / (2 * a));
        }
        return roots;
    }

    // clone() успадковується від Equation: super.clone() поверне QuadraticEquation,
    // а coeffs скопіюється в базовому clone().
    @Override
    public QuadraticEquation clone() {
        return (QuadraticEquation) super.clone();
    }
}
