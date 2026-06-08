/** Зупинено: відтворення починається з поточного треку. */
public class StoppedState implements State {
    @Override public void play(MediaPlayer player) {
        System.out.println("Playing: " + player.getCurrentTrack());
        player.setState(new PlayingState());
    }
    @Override public void pause(MediaPlayer player) {
        System.out.println("Can't pause: player is stopped");
    }
    @Override public void stop(MediaPlayer player) {
        System.out.println("Already stopped");
    }
    @Override public void next(MediaPlayer player) {
        System.out.println("Can't switch track: player is stopped");
    }
    @Override public void prev(MediaPlayer player) {
        System.out.println("Can't switch track: player is stopped");
    }
    @Override public String name() { return "Stopped"; }
}
