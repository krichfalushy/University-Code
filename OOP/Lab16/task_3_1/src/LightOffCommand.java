/** Команда: вимкнути світло конкретного світильника. */
public class LightOffCommand implements Command {
    private final Lamp lamp;
    public LightOffCommand(Lamp lamp) { this.lamp = lamp; }
    @Override public void execute() { lamp.lightOff(); }
}
