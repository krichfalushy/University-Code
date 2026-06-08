public class Main {
    public static void main(String[] args) {

        ShopItem pens = new ShopItem("pen", 175);
        ShopItem pencils = new ShopItem("pencil", 0);
        ShopItem paper = new ShopItem("paper", 500);

        Shop shop = new Shop();
        shop.putItem(pens);
        shop.putItem(pencils);
        shop.putItem(paper);

        System.out.println("=== By name (alphabetical) ===");
        for (ShopItem item : shop.byName()) {
            System.out.println(item);
        }

        System.out.println("\n=== By quantity (descending) ===");
        for (ShopItem item : shop.byQuantityDesc()) {
            System.out.println(item);
        }
    }
}
