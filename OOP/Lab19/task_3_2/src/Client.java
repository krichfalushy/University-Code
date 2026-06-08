import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        Triangle triangle = new Triangle(3, 3, 3);

        // Послідовність команд (за сценарієм умови)
        List<Command> commands = new ArrayList<>();
        commands.add(new SetCommand(triangle, 'b', 4));
        commands.add(new SetCommand(triangle, 'c', 5));
        commands.add(new SetCommand(triangle, 'a', 4));
        commands.add(new DuplicateCommand(triangle, 'a'));
        commands.add(new SetCommand(triangle, 'c', 6));
        commands.add(new SetCommand(triangle, 'b', 3));
        commands.add(new HalfCommand(triangle, 'a'));

        // Опікун: історія знімків станів + площі
        List<Memento> history = new ArrayList<>();
        List<Float> squares = new ArrayList<>();

        history.add(triangle.save());
        squares.add(triangle.square());
        System.out.println("Start: square = " + triangle.square());

        for (Command cmd : commands) {
            cmd.execute();
            history.add(triangle.save());
            squares.add(triangle.square());
            System.out.println("Square = " + triangle.square());
        }

        // Стан із НАЙМЕНШОЮ площею
        int min = 0;
        for (int i = 1; i < squares.size(); i++) {
            if (squares.get(i) < squares.get(min)) min = i;
        }

        triangle.restore(history.get(min));
        System.out.printf("%nMin square = %.4f, restored: %s%n", squares.get(min), triangle);
    }
}
