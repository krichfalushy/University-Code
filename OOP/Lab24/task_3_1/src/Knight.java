/** Лицар — новий персонаж; додається без зміни алгоритму захисту. */
public class Knight extends GameObject {
    @Override protected void pickUpWeapon()  { System.out.println("Pick up lance"); }
    @Override protected void defenseAction() { System.out.println("Defend with shield"); }
    @Override protected void moveToSafety()  { System.out.println("Return to the castle"); }
}
