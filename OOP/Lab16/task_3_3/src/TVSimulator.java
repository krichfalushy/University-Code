import java.util.ArrayList;
import java.util.List;

public class TVSimulator {
    public static void main(String[] args) {

        Device tv = new Television();
        Device radio = new Radio();

        // Пульт телевізора
        RemoteController controller = new RemoteController();
        controller.setDevice(tv);

        controller.deviceOn();
        for (int i = 0; i < 30; i++) {
            controller.deviceNextChanel();
        }
        controller.deviceVolumeUp();
        controller.deviceVolumeUp();
        controller.deviceVolumeUp();
        controller.devicePrevChanel();
        controller.deviceVolumeDown();
        controller.deviceOff();

        System.out.println("--- Universal remote: turn OFF all devices ---");
        // Увімкнемо обидва пристрої, щоб показати дію універсального вимкнення
        radio.on();
        tv.on();

        List<Command> offAll = new ArrayList<>();
        offAll.add(new OffCommand(tv));
        offAll.add(new OffCommand(radio));

        RemoteController universal = new RemoteController();
        universal.setOffCommand(new MacroCommand(offAll));
        universal.deviceOff();   // вимикає всі пристрої системи
    }
}
