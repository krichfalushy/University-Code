/**
 * Літак (Colleague). Не знає про смугу та інші літаки —
 * усі дії проходять через посередника ControlTower.
 */
public class Plane {

    private boolean isInTheAir;
    private final int id;
    private ControlTower tower;   // посилання на посередника

    public Plane(int id) {
        this.id = id;
        this.isInTheAir = false;
    }

    public void setTower(ControlTower tower) {
        this.tower = tower;
    }

    /** Зліт — лише через запит до вежі. */
    public void takeOff() {
        System.out.println("Plane " + id + " requests take-off...");
        if (tower.requestTakeOff(this)) {
            isInTheAir = true;
            System.out.println("Plane " + id + " is taking off...");
        }
    }

    /** Посадка — лише через запит до вежі. */
    public void land() {
        System.out.println("Plane " + id + " requests landing...");
        if (tower.requestLanding(this)) {
            isInTheAir = false;
            System.out.println("Plane " + id + " has landed.");
        }
    }

    public boolean getIsInTheAir() { return isInTheAir; }
    public int getId() { return id; }
}
