/** Лист — елемент із назвою та цінністю (value). */
public class Item implements Component {

    private final String name;
    private final int value;

    public Item(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public int getValue() { return value; }

    @Override
    public String toString() {
        return "Item{name='" + name + "', value=" + value + "}";
    }

    @Override
    public void execute() {
        System.out.println(this);
    }
}
