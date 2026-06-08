/**
 * Пульт дистанційного керування (Invoker).
 * Налаштовується командами; не знає, з яким пристроєм працює.
 */
public class RemoteController {
    private Command onCommand;
    private Command offCommand;
    private Command volumeUpCommand;
    private Command volumeDownCommand;
    private Command nextChanelCommand;
    private Command prevChanelCommand;

    public RemoteController() {}

    /** Швидке налаштування під конкретний пристрій. */
    public void setDevice(Device device) {
        onCommand = new OnCommand(device);
        offCommand = new OffCommand(device);
        volumeUpCommand = new VolumeUpCommand(device);
        volumeDownCommand = new VolumeDownCommand(device);
        nextChanelCommand = new NextChanelCommand(device);
        prevChanelCommand = new PrevChanelCommand(device);
    }

    public void setOffCommand(Command offCommand) { this.offCommand = offCommand; }

    void deviceOn()         { if (onCommand != null) onCommand.execute(); }
    void deviceOff()        { if (offCommand != null) offCommand.execute(); }
    void deviceVolumeUp()   { if (volumeUpCommand != null) volumeUpCommand.execute(); }
    void deviceVolumeDown() { if (volumeDownCommand != null) volumeDownCommand.execute(); }
    void deviceNextChanel() { if (nextChanelCommand != null) nextChanelCommand.execute(); }
    void devicePrevChanel() { if (prevChanelCommand != null) prevChanelCommand.execute(); }
}
