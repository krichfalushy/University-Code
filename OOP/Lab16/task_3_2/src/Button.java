/** Кнопка інтерфейсу (Invoker). Тримає команду й виконує її при кліку. */
public class Button {
    private Command command;
    public Button() {}
    public Button(Command command) { this.command = command; }
    public void setCommand(Command command) { this.command = command; }
    public void click() {
        if (command != null) command.execute();
    }
}
