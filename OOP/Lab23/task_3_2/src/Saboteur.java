/**
 * Диверсант. Мета — знищення секретної документації, особового складу та техніки.
 */
public class Saboteur implements Spy {

    @Override public void visit(GeneralStaff generalStaff) {
        System.out.println("Saboteur destroyed " + generalStaff.getSecretPaper() +
                " secret papers and eliminated " + generalStaff.getGenerals() + " generals.");
        generalStaff.setSecretPaper(0);
        generalStaff.setGenerals(0);
    }

    @Override public void visit(MilitaryBase militaryBase) {
        System.out.printf("Saboteur destroyed Military Base: " +
                "eliminated %d officers, %d soldiers; wrecked %d jeeps, %d tanks.%n",
                militaryBase.getOfficers(), militaryBase.getSoldiers(),
                militaryBase.getJeeps(), militaryBase.getTanks());
        militaryBase.setOfficers(0);
        militaryBase.setSoldiers(0);
        militaryBase.setJeeps(0);
        militaryBase.setTanks(0);
    }
}
