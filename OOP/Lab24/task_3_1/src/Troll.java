/** Троль — конкретна реалізація кроків. */
public class Troll extends GameObject {
    @Override protected void pickUpWeapon()  { System.out.println("Pick up club"); }
    @Override protected void defenseAction() { System.out.println("Defend with club"); }
    @Override protected void moveToSafety()  { System.out.println("Return to the mountain"); }
}
