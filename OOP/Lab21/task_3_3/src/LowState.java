/** Низька швидкість. */
public class LowState implements State {
    @Override public void turnUp(Fan fan) {
        fan.setState(new MediumState());
        System.out.println("Fan is on medium");
    }
    @Override public void turnDown(Fan fan) {
        // уже мінімальна — нічого не робимо
    }
    @Override public String name() { return "low"; }
}
