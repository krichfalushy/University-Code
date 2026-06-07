public class Canvas {
    public static void main(String[] args) {
        // Будь-яка комбінація тип × розмір — без окремих класів.
        Button[] buttons = {
            new CheckboxButton(new SmallSize()),
            new RadioButton(new MediumSize()),
            new DropdownButton(new LargeSize()),

            // нові розширення: ImageButton + UserSize, без правок наявних класів
            new ImageButton(new LargeSize(), "icons/save.png"),
            new RadioButton(new UserSize(120, 40)),
            new ImageButton(new UserSize(64, 64), "icons/play.png"),
        };
        for (Button b : buttons) {
            b.draw();
        }
    }
}
