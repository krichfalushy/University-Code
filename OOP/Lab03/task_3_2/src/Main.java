import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String[] files = {"input01.txt", "input02.txt", "input03.txt"};
        for (String file : files) {
            analyze(file);
        }
    }

    private static void analyze(String fileName) throws IOException {
        Path path = Path.of(fileName);
        if (!Files.exists(path)) {
            System.out.println("Файл не знайдено: " + fileName);
            return;
        }

        int[] byCount = new int[5];     // 0,1,2,3,4 розв'язків
        int infinite = 0;               // нескінченно багато

        // для рівнянь рівно з одним розв'язком
        double minRoot = Double.POSITIVE_INFINITY;
        double maxRoot = Double.NEGATIVE_INFINITY;
        Equation minEq = null, maxEq = null;

        for (String line : Files.readAllLines(path)) {
            line = line.trim();
            if (line.isEmpty()) continue;

            double[] coeffs = parse(line);
            Equation eq;
            try {
                eq = EquationFactory.create(coeffs);
            } catch (IllegalArgumentException e) {
                continue;   // пропускаємо рядки з неприпустимою кількістю коеф.
            }

            List<Double> roots = eq.solve();
            int count = (roots == null) ? Equation.INFINITE_SOLUTIONS : roots.size();

            if (count == Equation.INFINITE_SOLUTIONS) {
                infinite++;
            } else if (count >= 0 && count <= 4) {
                byCount[count]++;
            }

            if (count == 1) {
                double r = roots.get(0);
                if (r < minRoot) { minRoot = r; minEq = eq; }
                if (r > maxRoot) { maxRoot = r; maxEq = eq; }
            }
        }

        System.out.println("==================== " + fileName + " ====================");
        System.out.println("Без розв'язків:            " + byCount[0]);
        System.out.println("Один розв'язок:            " + byCount[1]);
        System.out.println("Два розв'язки:             " + byCount[2]);
        System.out.println("Три розв'язки:             " + byCount[3]);
        System.out.println("Чотири розв'язки:          " + byCount[4]);
        System.out.println("Нескінченно багато:        " + infinite);
        if (minEq != null) {
            System.out.printf("Найменший розв'язок: %.4f  -> рівняння %s%n", minRoot, minEq);
            System.out.printf("Найбільший розв'язок: %.4f  -> рівняння %s%n", maxRoot, maxEq);
        } else {
            System.out.println("Рівнянь рівно з одним розв'язком немає.");
        }
        System.out.println();
    }

    /** Розбір рядка коефіцієнтів, розділених пробілами. */
    private static double[] parse(String line) {
        String[] parts = line.trim().split("\\s+");
        List<Double> nums = new ArrayList<>();
        for (String p : parts) {
            if (!p.isEmpty()) nums.add(Double.parseDouble(p));
        }
        double[] result = new double[nums.size()];
        for (int i = 0; i < result.length; i++) result[i] = nums.get(i);
        return result;
    }
}
