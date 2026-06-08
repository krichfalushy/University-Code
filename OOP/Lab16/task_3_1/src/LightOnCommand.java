/** Команда: увімкнути світло конкретного світильника. */
public class LightOnCommand implements Command {
    private final Lamp lamp;
    public LightOnCommand(Lamp lamp) { this.lamp = lamp; }
    @Override public void execute() { lamp.lightOn(); }
}
