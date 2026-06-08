public class PrevChanelCommand implements Command {
    private final Device device;
    public PrevChanelCommand(Device device) { this.device = device; }
    @Override public void execute() { device.prevChanel(); }
}
