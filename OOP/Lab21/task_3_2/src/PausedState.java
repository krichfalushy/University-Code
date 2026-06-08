/** Пауза: відновлення відтворення, стоп, перемикання треків. */
public class PausedState implements State {
    @Override public void play(MediaPlayer player) {
        System.out.println("Resumed: " + player.getCurrentTrack());
        player.setState(new PlayingState());
    }
    @Override public void pause(MediaPlayer player) {
        System.out.println("Already paused");
    }
    @Override public void stop(MediaPlayer player) {
        System.out.println("Stopped");
        player.setTrackNum(0);
        player.setState(new StoppedState());
    }
    @Override public void next(MediaPlayer player) {
        player.nextTrack();
        System.out.println("Switched (paused) to: " + player.getCurrentTrack());
    }
    @Override public void prev(MediaPlayer player) {
        player.prevTrack();
        System.out.println("Switched (paused) to: " + player.getCurrentTrack());
    }
    @Override public String name() { return "Paused"; }
}
