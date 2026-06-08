/** Команда встановлення сторони (a/b/c). */
public class SetCommand extends TriangleCommand {
    private final char edge;
    private final float value;
    public SetCommand(Triangle t, char edge, float value) {
        super(t); this.edge = edge; this.value = value;
    }
    @Override protected void doExecute() {
        switch (edge) {
            case 'a': triangle.setA(value); break;
            case 'b': triangle.setB(value); break;
            case 'c': triangle.setC(value); break;
        }
    }
}
