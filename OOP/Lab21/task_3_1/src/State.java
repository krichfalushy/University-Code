/** Стан плеєра (State). Інкапсулює поведінку play/pause для конкретного стану. */
public interface State {
    void play(MediaPlayer player);
    void pause(MediaPlayer player);
}
