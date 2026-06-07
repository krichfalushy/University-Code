/** Компонент напою (інгредієнт) із власною собівартістю. */
public class Ingredient {
    private final String name;
    private final double cost;   // собівартість компонента, грн

    public Ingredient(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() { return name; }
    public double getCost() { return cost; }
}
