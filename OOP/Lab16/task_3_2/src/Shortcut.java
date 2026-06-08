/** Комбінація клавіш (Invoker). */
public class Shortcut {
    private Command command;
    public Shortcut() {}
    public Shortcut(Command command) { this.command = command; }
    public void setCommand(Command command) { this.command = command; }
    public void press() {
        if (command != null) command.execute();
    }
}
