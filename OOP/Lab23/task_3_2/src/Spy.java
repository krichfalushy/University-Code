/** Шпигун (Visitor). Абстрактні методи для відвідування кожного типу об'єкта. */
public interface Spy {
    void visit(GeneralStaff generalStaff);
    void visit(MilitaryBase militaryBase);
}
