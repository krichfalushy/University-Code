public class Cafe {
    public static void main(String[] args) {
        // Будь-яка комбінація напій × спосіб × подача — без окремих класів.
        Beverage[] menu = {
            new Coffee(3, new BlackPreparation(200), Serving.DINE_IN),
            new Coffee(3, new MilkPreparation(200),  Serving.TAKEAWAY),
            new Tee(0,    new BlackPreparation(300), Serving.TAKEAWAY),
            new Tee(2,    new MilkPreparation(300),  Serving.DINE_IN),
            new Chocolate(3, new BlackPreparation(200), Serving.DINE_IN),
            new Chocolate(3, new MilkPreparation(200),  Serving.TAKEAWAY),
        };
        for (Beverage b : menu) {
            beverageInfo(b);
        }
    }

    private static void beverageInfo(Beverage beverage) {
        System.out.println("=========================");
        beverage.prepare();
        System.out.printf("Cost of beverage: %s grn%n", beverage.cost());
        beverage.drink();
    }
}
