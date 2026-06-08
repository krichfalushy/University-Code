public class NextChanelCommand implements Command {
    private final Device device;
    public NextChanelCommand(Device device) { this.device = device; }
    @Override public void execute() { device.nextChanel(); }
}
