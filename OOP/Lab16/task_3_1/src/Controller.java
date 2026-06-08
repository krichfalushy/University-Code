/**
 * Пульт (Invoker). Зберігає команди увімкнення/вимкнення
 * і виконує їх, не знаючи деталей одержувача.
 */
public class Controller {
    private Command onCommand;
    private Command offCommand;

    public Controller() {
    }

    public Controller(Command onCommand, Command offCommand) {
        this.onCommand = onCommand;
        this.offCommand = offCommand;
    }

    public void setCommands(Command onCommand, Command offCommand) {
        this.onCommand = onCommand;
        this.offCommand = offCommand;
    }

    public void on() {
        if (onCommand != null) onCommand.execute();
    }

    public void off() {
        if (offCommand != null) offCommand.execute();
    }
}
