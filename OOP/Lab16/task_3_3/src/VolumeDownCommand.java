public class VolumeDownCommand implements Command {
    private final Device device;
    public VolumeDownCommand(Device device) { this.device = device; }
    @Override public void execute() { device.volumeDown(); }
}
