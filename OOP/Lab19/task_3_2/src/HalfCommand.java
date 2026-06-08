/** Команда зменшення сторони вдвічі. */
public class HalfCommand extends TriangleCommand {
    private final char edge;
    public HalfCommand(Triangle t, char edge) { super(t); this.edge = edge; }
    @Override protected void doExecute() {
        switch (edge) {
            case 'a': triangle.halfA(); break;
            case 'b': triangle.halfB(); break;
            case 'c': triangle.halfC(); break;
        }
    }
}
