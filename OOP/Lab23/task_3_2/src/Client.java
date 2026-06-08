import java.util.ArrayList;
import java.util.List;

public class Client {

    public static void main(String[] args) {
        final List<MilitaryObject> militaryObjects = new ArrayList<>();
        militaryObjects.add(new GeneralStaff(20, 100));
        militaryObjects.add(new MilitaryBase(10, 1000, 300, 20));

        System.out.println("=== Before ===");
        militaryObjects.forEach(System.out::println);

        // Секретний агент відвідує об'єкти
        System.out.println("\n=== Secret Agent visit ===");
        SecretAgent agent = new SecretAgent();
        for (MilitaryObject m : militaryObjects) {
            m.accept(agent);
        }
        System.out.println("Total stolen papers: " + agent.getStolenPapers());

        System.out.println("\n=== State after agent ===");
        militaryObjects.forEach(System.out::println);

        // Диверсант відвідує об'єкти
        System.out.println("\n=== Saboteur visit ===");
        Saboteur saboteur = new Saboteur();
        for (MilitaryObject m : militaryObjects) {
            m.accept(saboteur);
        }

        System.out.println("\n=== State after saboteur ===");
        militaryObjects.forEach(System.out::println);
    }
}
