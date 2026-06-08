/** Висока швидкість. */
public class HighState implements State {
    @Override public void turnUp(Fan fan) {
        // уже максимальна — нічого не робимо
    }
    @Override public void turnDown(Fan fan) {
        fan.setState(new MediumState());
        System.out.println("Fan is on medium");
    }
    @Override public String name() { return "high"; }
}
