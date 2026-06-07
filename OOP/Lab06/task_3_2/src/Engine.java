/** Двигун. Прототип: clone() створює копію (поля примітивні/незмінні enum). */
public class Engine implements Cloneable {
    public enum Fuel { Petrol, Diesel, Electric }

    private int power;
    private int torque;
    private Fuel fuel;
    private float volume;

    public Engine(int power, int torque, Fuel fuel, float volume) {
        this.power = power;
        this.torque = torque;
        this.fuel = fuel;
        this.volume = volume;
    }

    public void setPower(int power) { this.power = power; }
    public void setFuel(Fuel fuel)  { this.fuel = fuel; }

    @Override
    public Engine clone() {
        try {
            return (Engine) super.clone();   // примітиви + enum копіюються коректно
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String toString() {
        return "Engine{power=" + power + ", torque=" + torque +
                ", fuel=" + fuel + ", volume=" + volume + "}";
    }
}
