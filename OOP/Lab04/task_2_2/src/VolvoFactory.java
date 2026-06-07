public class VolvoFactory implements TransportFactory {
    public Bus createBus() { return new VolvoBus(); }
    public Tram createTram() { return new VolvoTram(); }
    public Trolleybus createTrolleybus() { return new VolvoTrolleybus(); }
    public String getManufacturer() { return "Volvo"; }
}
