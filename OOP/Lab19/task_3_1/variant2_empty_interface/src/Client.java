import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        Triangle triangle = new Triangle(3, 3, 3);

        String[] edges = {"b", "c", "a", "c", "b"};
        float[] values = {4, 5, 4, 2, 3};

        // Опікун зберігає Memento лише як порожній інтерфейс
        List<Memento> history = new ArrayList<>();
        List<Float> squares = new ArrayList<>();

        history.add(triangle.save());
        squares.add(triangle.square());
        System.out.println("Start: square = " + triangle.square());

        for (int i = 0; i < edges.length; i++) {
            switch (edges[i]) {
                case "a": triangle.setA(values[i]); break;
                case "b": triangle.setB(values[i]); break;
                case "c": triangle.setC(values[i]); break;
            }
            history.add(triangle.save());
            squares.add(triangle.square());
            System.out.println("Square = " + triangle.square());
        }

        int best = 0;
        for (int i = 1; i < squares.size(); i++) {
            if (squares.get(i) > squares.get(best)) best = i;
        }

        triangle.restore(history.get(best));
        System.out.printf("%nMax square = %.4f, restored: %s%n", squares.get(best), triangle);
    }
}
