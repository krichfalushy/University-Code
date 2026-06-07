public class Main {
    public static void main(String[] args) {
        CoffeeShop shop = new CoffeeShop();

        // Меню з собівартістю/ціною/прибутком кожного напою
        System.out.println("=== Меню кав'ярні (MechMat Mall) ===");
        for (BeverageType type : BeverageType.values()) {
            System.out.println(shop.sell(type));
        }

        // Імітація продажів за певний період
        BeverageType[] dayOrders = {
            BeverageType.ESPRESSO, BeverageType.LATTE, BeverageType.LATTE,
            BeverageType.AMERICANO, BeverageType.CAPPUCCINO, BeverageType.ESPRESSO,
            BeverageType.HOT_CHOCOLATE, BeverageType.CAPPUCCINO, BeverageType.LATTE,
            BeverageType.AMERICANO
        };

        System.out.println("\n=== Продажі за день ===");
        for (BeverageType order : dayOrders) {
            shop.sell(order);
        }

        shop.printReport();
    }
}
