package utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;

import java.net.URL;

public class SoundManager {

	private static MediaPlayer backgroundPlayer;

	public static final AudioClip SOUND_POP = loadSound("Pop.mp3");
	public static final AudioClip SOUND_FART = loadSound("Fart.mp3");
	public static final AudioClip SOUND_SUCCESS = loadSound("Success.mp3");

	public static void playBackgroundMusic(String filename, boolean loop) {
		stopBackgroundMusic();

		URL resource = SoundManager.class.getResource("/assets/sounds/" + filename);
		if (resource == null) {
			System.out.println("Sound file not found: " + filename);
			return;
		}

		Media media = new Media(resource.toString());
		backgroundPlayer = new MediaPlayer(media);
		backgroundPlayer.setVolume(0.7);
		if (loop)
			backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		backgroundPlayer.play();
	}

	public static void stopBackgroundMusic() {
		if (backgroundPlayer != null) {
			backgroundPlayer.stop();
		}
	}

	public static void play(AudioClip clip) {
		if (clip != null) {
			clip.play();
		}
	}

	private static AudioClip loadSound(String filename) {
		URL resource = SoundManager.class.getResource("/assets/sounds/" + filename);
		if (resource == null) {
			System.out.println("Sound file not found: " + filename);
			return null;
		}
		return new AudioClip(resource.toString());
	}
}
