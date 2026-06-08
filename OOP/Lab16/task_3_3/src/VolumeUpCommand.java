public class VolumeUpCommand implements Command {
    private final Device device;
    public VolumeUpCommand(Device device) { this.device = device; }
    @Override public void execute() { device.volumeUp(); }
}
