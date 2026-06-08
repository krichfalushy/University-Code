import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {

        Lamp kitchenLamp = new Lamp("Kitchen");
        Lamp hallLamp = new Lamp("Hall");
        Lamp bedroomLamp = new Lamp("Bedroom");
        Lamp bathroomLamp = new Lamp("Bathroom");

        // Пульти окремих світильників
        Controller controllerKitchenLamp  = new Controller(new LightOnCommand(kitchenLamp),  new LightOffCommand(kitchenLamp));
        Controller controllerHallLamp     = new Controller(new LightOnCommand(hallLamp),     new LightOffCommand(hallLamp));
        Controller controllerBedroomLamp  = new Controller(new LightOnCommand(bedroomLamp),  new LightOffCommand(bedroomLamp));
        Controller controllerBathroomLamp = new Controller(new LightOnCommand(bathroomLamp), new LightOffCommand(bathroomLamp));

        // Універсальний пульт: off вимикає УСІ світильники (макрокоманда)
        List<Command> offAll = new ArrayList<>();
        for (Lamp lamp : new Lamp[]{kitchenLamp, hallLamp, bedroomLamp, bathroomLamp}) {
            offAll.add(new LightOffCommand(lamp));
        }
        Controller controllerUniversal = new Controller(null, new MacroCommand(offAll));

        // Simulation
        controllerKitchenLamp.on();
        controllerHallLamp.on();
        controllerBedroomLamp.on();

        controllerKitchenLamp.off();
        controllerHallLamp.off();
        controllerBedroomLamp.off();

        controllerBedroomLamp.on();
        controllerBathroomLamp.on();

        System.out.println("--- Universal OFF ---");
        controllerUniversal.off();   // вимикає всі, що горять (Bedroom, Bathroom)
    }
}
