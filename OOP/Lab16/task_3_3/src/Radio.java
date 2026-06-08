public class Radio implements Device {

    private int volume;
    private int station;
    private boolean isOn;

    public Radio() {
        this.isOn = false;
        this.volume = 30;
        this.station = 1;
    }

    @Override public void on() {
        if (isOn) return;
        System.out.println("Radio is on");
        isOn = true;
    }

    @Override public void off() {
        if (!isOn) return;
        System.out.println("Radio is off");
        isOn = false;
    }

    @Override public void volumeUp() {
        if (!isOn) return;
        volume++;
        System.out.println("Radio Volume = " + volume);
    }

    @Override public void volumeDown() {
        if (!isOn) return;
        volume--;
        System.out.println("Radio Volume = " + volume);
    }

    @Override public void nextChanel() {
        if (!isOn) return;
        station++;
        System.out.println("Radio Station = " + station);
    }

    @Override public void prevChanel() {
        if (!isOn) return;
        station--;
        System.out.println("Radio Station = " + station);
    }
}
