package utils;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class SceneSwitcher {

	public static void switchTo(String fxmlPath, Window window) {
		try {
			FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
			Parent root = loader.load();

			Scene newScene = new Scene(root);

			// Fade in transition
			FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), root);
			fadeIn.setFromValue(0.0);
			fadeIn.setToValue(1.0);
			fadeIn.play();

			Stage stage = (Stage) window;
			stage.setScene(newScene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
