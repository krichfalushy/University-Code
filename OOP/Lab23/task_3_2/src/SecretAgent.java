/**
 * Секретний агент. Мета — збір/викрадення секретної інформації.
 * На Генштабі викрадає секретні папери; на Військовій базі — фіксує дані
 * про особовий склад і техніку (розвідка), не знищуючи їх.
 */
public class SecretAgent implements Spy {

    private int stolenPapers = 0;

    @Override public void visit(GeneralStaff generalStaff) {
        int papers = generalStaff.getSecretPaper();
        stolenPapers += papers;
        generalStaff.setSecretPaper(0);   // папери викрадено
        System.out.println("SecretAgent stole " + papers + " secret papers from General Staff.");
    }

    @Override public void visit(MilitaryBase militaryBase) {
        // Збір розвідданих без руйнувань
        System.out.printf("SecretAgent collected intel from Military Base: " +
                "officers=%d, soldiers=%d, jeeps=%d, tanks=%d%n",
                militaryBase.getOfficers(), militaryBase.getSoldiers(),
                militaryBase.getJeeps(), militaryBase.getTanks());
    }

    public int getStolenPapers() { return stolenPapers; }
}
