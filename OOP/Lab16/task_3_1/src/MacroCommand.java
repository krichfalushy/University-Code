import java.util.List;

/** Складена команда: виконує послідовно кілька команд (напр., "вимкнути все"). */
public class MacroCommand implements Command {
    private final List<Command> commands;
    public MacroCommand(List<Command> commands) { this.commands = commands; }
    @Override public void execute() {
        for (Command c : commands) c.execute();
    }
}
