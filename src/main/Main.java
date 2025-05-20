package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Config;

import java.io.IOException;

public class Main extends Application {

	private static final int WIDTH = Config.SCREEN_WIDTH;
	private static final int HEIGHT = Config.SCREEN_WIDTH;
	private static final int FRAME_INTERVAL_MS = 1000 / 60;

	@Override
	public void start(Stage stage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/ui/StartPage.fxml"));
			Scene scene = new Scene(root, Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT); // start screen size
			stage.setScene(scene);
			stage.setTitle("DNA Factory");

			// Frame rate limiter for the menu
			Timeline timeline = new Timeline(new KeyFrame(Duration.millis(FRAME_INTERVAL_MS), event -> {
				// Update logic for the menu (if any)
			}));
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();

			stage.setOnCloseRequest(event -> {
				// Handle window close event
				if (SimulationThread.getInstance() != null) {
					SimulationThread.getInstance().stop();
				}
			});

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
