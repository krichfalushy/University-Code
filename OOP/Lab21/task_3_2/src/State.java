/** Стан музичного плеєра. Кожна дія має сенс залежно від стану. */
public interface State {
    void play(MediaPlayer player);
    void pause(MediaPlayer player);
    void stop(MediaPlayer player);
    void next(MediaPlayer player);
    void prev(MediaPlayer player);
    String name();
}
