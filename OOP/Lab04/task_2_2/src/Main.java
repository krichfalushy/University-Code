import java.util.List;

public class Main {
    public static void main(String[] args) {
        final int A  = 10;        // автобуси
        final int T  = 5;         // трамваї
        final int Tr = 40;        // тролейбуси
        final int N  = 200_000;   // пробіг, км (200 тис. км)

        List<TransportFactory> manufacturers = List.of(
                new VolvoFactory(), new SkodaFactory(), new HyundaiFactory());

        TransportFleet best = null;
        long bestCost = Long.MAX_VALUE;

        System.out.printf("Закупка: %d автобусів, %d трамваїв, %d тролейбусів; пробіг %,d км%n%n", A, T, Tr, N);
        for (TransportFactory f : manufacturers) {
            TransportFleet fleet = new TransportFleet(f, A, T, Tr);
            long cost = fleet.totalContractCost(N);
            System.out.printf("%-8s : повна вартість контракту = %,d грн%n", fleet.getManufacturer(), cost);
            if (cost < bestCost) { bestCost = cost; best = fleet; }
        }

        System.out.printf("%nНайвигідніший виробник: %s (%,d грн)%n", best.getManufacturer(), bestCost);
    }
}
