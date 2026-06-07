/** Абстрактна фабрика виробника транспорту. */
public interface TransportFactory {
    Bus createBus();
    Tram createTram();
    Trolleybus createTrolleybus();
    String getManufacturer();
}
