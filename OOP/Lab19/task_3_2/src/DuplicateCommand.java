/** Команда збільшення сторони вдвічі. */
public class DuplicateCommand extends TriangleCommand {
    private final char edge;
    public DuplicateCommand(Triangle t, char edge) { super(t); this.edge = edge; }
    @Override protected void doExecute() {
        switch (edge) {
            case 'a': triangle.duplicateA(); break;
            case 'b': triangle.duplicateB(); break;
            case 'c': triangle.duplicateC(); break;
        }
    }
}
