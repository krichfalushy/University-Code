/**
 * Фабрика рівнянь. За довжиною списку коефіцієнтів створює
 * відповідний тип рівняння:
 *   2 -> лінійне (Equation)
 *   3 -> квадратне (QuadraticEquation)
 *   5 -> біквадратне (BiQuadraticEquation)
 */
public class EquationFactory {

    public static Equation create(double[] coeffs) {
        switch (coeffs.length) {
            case 2:  return new Equation(coeffs);
            case 3:  return new QuadraticEquation(coeffs);
            case 5:  return new BiQuadraticEquation(coeffs);
            default:
                throw new IllegalArgumentException(
                        "Непідтримувана кількість коефіцієнтів: " + coeffs.length);
        }
    }
}
