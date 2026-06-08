public class Main {
    public static void main(String[] args) {
        GameObject pirate = new Pirate();
        pirate.defendAgainstAttack();

        GameObject troll = new Troll();
        troll.defendAgainstAttack();

        GameObject knight = new Knight();   // новий персонаж
        knight.defendAgainstAttack();
    }
}
