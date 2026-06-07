public class Main {
    public static void main(String[] args) {
        // Еспресо з двома порціями цукру
        Beverage b1 = new Espresso();
        b1 = new Sugar(b1);
        b1 = new Sugar(b1);
        System.out.println(b1);

        // Чорна кава з вершками та двома порціями цукру
        Beverage b2 = new DarkRoast();
        b2 = new Cream(b2);
        b2 = new Sugar(b2);
        b2 = new Sugar(b2);
        System.out.println(b2);

        // Чорна кава з кремом та порцією цукру
        Beverage b3 = new DarkRoast();
        b3 = new Whip(b3);
        b3 = new Sugar(b3);
        System.out.println(b3);

        // Кава без кофеїну з молоком та двома порціями цукру
        Beverage b4 = new Decaf();
        b4 = new Milk(b4);
        b4 = new Sugar(b4);
        b4 = new Sugar(b4);
        System.out.println(b4);
    }
}
