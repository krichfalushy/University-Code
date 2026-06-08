/**
 * Базова команда. Перед зміною зберігає знімок трикутника (Memento),
 * а undo() відновлює стан із цього знімка — зв'язка Команда + Знімок.
 */
public abstract class TriangleCommand implements Command {
    protected final Triangle triangle;
    private Memento backup;

    protected TriangleCommand(Triangle triangle) {
        this.triangle = triangle;
    }

    @Override
    public void execute() {
        backup = triangle.save();   // зберігаємо стан ДО зміни
        doExecute();
    }

    @Override
    public void undo() {
        if (backup != null) {
            triangle.restore(backup);
        }
    }

    protected abstract void doExecute();
}
