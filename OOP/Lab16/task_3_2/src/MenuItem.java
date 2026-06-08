/** Пункт меню (Invoker). */
public class MenuItem {
    private Command command;
    public MenuItem() {}
    public MenuItem(Command command) { this.command = command; }
    public void setCommand(Command command) { this.command = command; }
    public void select() {
        if (command != null) command.execute();
    }
}
