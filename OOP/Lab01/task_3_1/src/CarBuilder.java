/**
 * Будівельник (Builder). Покроково створює Car: ставить двигун та колеса.
 * Залежить (пунктирні стрілки на діаграмі) від Car, Wheel та Engine.
 */
public class CarBuilder {
    private Car car;

    public void createNewCar() {
        car = new Car();
    }

    public void buildEngine(String type) {
        car.setEngine(new Engine(type));
    }

    public void buildWheels(int count, int radius) {
        for (int i = 0; i < count; i++) {
            car.addWheel(new Wheel(radius));
        }
    }

    public Car getCar() {
        return car;
    }
}
