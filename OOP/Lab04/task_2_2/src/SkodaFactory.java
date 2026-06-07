public class SkodaFactory implements TransportFactory {
    public Bus createBus() { return new SkodaBus(); }
    public Tram createTram() { return new SkodaTram(); }
    public Trolleybus createTrolleybus() { return new SkodaTrolleybus(); }
    public String getManufacturer() { return "Skoda"; }
}
