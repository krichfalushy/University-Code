import java.util.ArrayList;
import java.util.List;

/** Біквадратне рівняння a*x^4 + b*x^2 + c = 0. coeffs = [a, 0, b, 0, c]. */
public class BiQuadraticEquation extends Equation {

    public BiQuadraticEquation(double... coeffs) {
        super(coeffs);
    }

    @Override
    public List<Double> solve() {
        double a = coeffs[0], b = coeffs[2], c = coeffs[4];
        if (a == 0) return new QuadraticEquation(b, 0, c).solve();

        List<Double> tRoots = new QuadraticEquation(a, b, c).solve();
        if (tRoots == null) return null;

        List<Double> roots = new ArrayList<>();
        for (double t : tRoots) {
            if (t > 0) {
                double x = Math.sqrt(t);
                roots.add(-x);
                roots.add(x);
            } else if (t == 0) {
                roots.add(0.0);
            }
        }
        return roots;
    }

    @Override
    public BiQuadraticEquation clone() {
        return (BiQuadraticEquation) super.clone();
    }
}
