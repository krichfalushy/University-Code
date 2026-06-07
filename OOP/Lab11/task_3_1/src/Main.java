public class Main {
    public static void main(String[] args) {
        // Починаємо з порожнього базового рядка
        Printable s = new PrintableString("");

        // "Hello" + "," + " " + "World" + "!"
        s = new PostWordDecorator(s, "Hello");   // Hello
        s = new PostComaDecorator(s);            // Hello,
        s = new PostSpaceDecorator(s);           // Hello,_
        s = new PostWordDecorator(s, "World");   // Hello, World
        s = new PostExclaimDecorator(s);         // Hello, World!

        s.print();
        System.out.println();

        // Демонстрація PreWordDecorator та PostEndlDecorator
        Printable greeting = new PrintableString("");
        greeting = new PostWordDecorator(greeting, "World");
        greeting = new PreWordDecorator(greeting, "Hello ");   // "Hello World"
        greeting = new PostEndlDecorator(greeting);
        greeting.print();
        System.out.print("(end)");
    }
}
