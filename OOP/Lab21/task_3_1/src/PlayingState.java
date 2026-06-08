/** Стан "відтворення". */
public class PlayingState implements State {
    @Override public void play(MediaPlayer player) {
        // вже відтворюється — нічого не робимо
    }
    @Override public void pause(MediaPlayer player) {
        player.setIcon("play button");
        System.out.println("Video paused, icon set to " + player.getIcon());
        player.setState(new PausedState());
    }
    @Override public String toString() { return "playing"; }
}
