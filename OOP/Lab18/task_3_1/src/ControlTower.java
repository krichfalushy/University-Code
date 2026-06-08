import java.util.ArrayList;
import java.util.List;

/**
 * Посередник (Mediator) — диспетчерська вежа.
 * Централізує взаємодію між літаками та злітно-посадковою смугою:
 * літаки не спілкуються напряму зі смугою чи списками одне одного,
 * а лише надсилають запити вежі.
 */
public class ControlTower {

    private final Runway runway = new Runway();
    private final List<Plane> planesInFlight = new ArrayList<>();
    private final List<Plane> planesOnGround = new ArrayList<>();

    /** Реєстрація літака в системі (спочатку — на землі). */
    public void register(Plane plane) {
        planesOnGround.add(plane);
        plane.setTower(this);
    }

    /** Запит на зліт. Вежа вирішує, чи дозволити. */
    public boolean requestTakeOff(Plane plane) {
        if (plane.getIsInTheAir()) {
            System.out.println("Tower: plane " + plane.getId() + " is already in the air.");
            return false;
        }
        if (!runway.getIsAvailable()) {
            System.out.println("Tower: runway busy, plane " + plane.getId() + " hold position.");
            return false;
        }
        System.out.println("Tower: take-off cleared for plane " + plane.getId());
        planesOnGround.remove(plane);
        planesInFlight.add(plane);
        runway.setIsAvailable(false);
        // звільняємо смугу після відриву від землі
        runway.setIsAvailable(true);
        return true;
    }

    /** Запит на посадку. */
    public boolean requestLanding(Plane plane) {
        if (!plane.getIsInTheAir()) {
            System.out.println("Tower: plane " + plane.getId() + " is already on the ground.");
            return false;
        }
        if (!runway.getIsAvailable()) {
            System.out.println("Tower: runway busy, plane " + plane.getId() + " keep circling.");
            return false;
        }
        System.out.println("Tower: landing cleared for plane " + plane.getId());
        planesInFlight.remove(plane);
        planesOnGround.add(plane);
        return true;
    }

    public int planesInFlightCount() { return planesInFlight.size(); }
    public int planesOnGroundCount() { return planesOnGround.size(); }
}
