import java.util.ArrayList;
import java.util.List;

/**
 * Парк муніципального транспорту. Формується через абстрактну фабрику
 * (один виробник на весь парк) і рахує повну вартість контракту:
 * закупка + експлуатація на N км.
 */
public class TransportFleet {
    private final TransportFactory factory;
    private final List<Bus> buses = new ArrayList<>();
    private final List<Tram> trams = new ArrayList<>();
    private final List<Trolleybus> trolleybuses = new ArrayList<>();

    public TransportFleet(TransportFactory factory, int busCount, int tramCount, int trolleyCount) {
        this.factory = factory;
        for (int i = 0; i < busCount; i++)     buses.add(factory.createBus());
        for (int i = 0; i < tramCount; i++)    trams.add(factory.createTram());
        for (int i = 0; i < trolleyCount; i++) trolleybuses.add(factory.createTrolleybus());
    }

    /** Повна вартість = закупка + експлуатація на mileage км. */
    public long totalContractCost(int mileageKm) {
        long total = 0;
        for (Bus b : buses)         total += (long) b.getCost() + (long) b.getUsageCost() * mileageKm;
        for (Tram t : trams)        total += (long) t.getCost() + (long) t.getUsageCost() * mileageKm;
        for (Trolleybus tr : trolleybuses) total += (long) tr.getCost() + (long) tr.getUsageCost() * mileageKm;
        return total;
    }

    public String getManufacturer() { return factory.getManufacturer(); }
}
