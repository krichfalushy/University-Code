/** Команда над трикутником із підтримкою скасування (undo) через Знімок. */
public interface Command {
    void execute();
    void undo();
}
