/** Відтворення: пауза, стоп, перемикання треків. */
public class PlayingState implements State {
    @Override public void play(MediaPlayer player) {
        System.out.println("Already playing: " + player.getCurrentTrack());
    }
    @Override public void pause(MediaPlayer player) {
        System.out.println("Paused: " + player.getCurrentTrack());
        player.setState(new PausedState());
    }
    @Override public void stop(MediaPlayer player) {
        System.out.println("Stopped");
        player.setTrackNum(0);
        player.setState(new StoppedState());
    }
    @Override public void next(MediaPlayer player) {
        player.nextTrack();
        System.out.println("Playing next: " + player.getCurrentTrack());
    }
    @Override public void prev(MediaPlayer player) {
        player.prevTrack();
        System.out.println("Playing prev: " + player.getCurrentTrack());
    }
    @Override public String name() { return "Playing"; }
}
