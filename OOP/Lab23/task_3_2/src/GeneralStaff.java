public class GeneralStaff extends MilitaryObject {

    private int generals;
    private int secretPaper;

    public GeneralStaff(int generals, int secretPaper) {
        this.generals = generals;
        this.secretPaper = secretPaper;
    }

    public int getGenerals() { return generals; }
    public void setGenerals(int generals) { this.generals = generals; }
    public int getSecretPaper() { return secretPaper; }
    public void setSecretPaper(int secretPaper) { this.secretPaper = secretPaper; }

    @Override public void accept(Spy spy) { spy.visit(this); }

    @Override public String toString() {
        return "GeneralStaff{generals=" + generals + ", secretPaper=" + secretPaper + "}";
    }
}
