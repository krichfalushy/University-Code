import java.util.List;

public class Main {
    public static void main(String[] args) {
        final int N = 60;   // горизонт у днях

        // Продажі на день: [еспресо, капучіно, латте]
        int[] dailySales = {40, 30, 20};

        List<CoffeeMachineFactory> manufacturers = List.of(
                new DeLonghiFactory(), new JuraFactory());

        System.out.printf("Горизонт: %d днів; продажі/день: еспресо=%d, капучіно=%d, латте=%d%n%n",
                N, dailySales[0], dailySales[1], dailySales[2]);

        CoffeeShop best = null;
        double bestProfit = -Double.MAX_VALUE;

        for (CoffeeMachineFactory f : manufacturers) {
            CoffeeShop shop = new CoffeeShop(f);
            double profit = shop.netProfit(N, dailySales);
            int breakEven = shop.breakEvenDay(3650, dailySales);

            System.out.printf("%-9s | машина=%,.0f грн, обслуг.=%,.0f грн/день%n",
                    shop.getManufacturer(),
                    shop.getMachine().getMachineCost(),
                    shop.getMachine().getDailyMaintenance());
            System.out.printf("          | чистий прибуток за %d дн: %,.2f грн%n", N, profit);
            System.out.printf("          | вихід у плюс: %s%n%n",
                    breakEven > 0 ? ("день " + breakEven) : "не виходить");

            if (profit > bestProfit) { bestProfit = profit; best = shop; }
        }

        System.out.printf("Оптимальний виробник за %d днів: %s (%,.2f грн)%n",
                N, best.getManufacturer(), bestProfit);
    }
}
