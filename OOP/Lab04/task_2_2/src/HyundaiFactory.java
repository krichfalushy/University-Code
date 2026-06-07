public class HyundaiFactory implements TransportFactory {
    public Bus createBus() { return new HyundaiBus(); }
    public Tram createTram() { return new HyundaiTram(); }
    public Trolleybus createTrolleybus() { return new HyundaiTrolleybus(); }
    public String getManufacturer() { return "Hyundai"; }
}
