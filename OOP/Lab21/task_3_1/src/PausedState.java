/** Стан "на паузі". */
public class PausedState implements State {
    @Override public void play(MediaPlayer player) {
        player.setIcon("pause button");
        System.out.println("Video playing, icon set to " + player.getIcon());
        player.setState(new PlayingState());
    }
    @Override public void pause(MediaPlayer player) {
        // вже на паузі — нічого не робимо
    }
    @Override public String toString() { return "paused"; }
}
