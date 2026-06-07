public class Main {
    public static void main(String[] args) {
        Creator creator1 = new ConcreteCreator1();
        Creator creator2 = new ConcreteCreator2();

        creator1.someOperation();
        creator2.someOperation();
    }
}
