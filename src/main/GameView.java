package main;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import ui.Toolbar;
import utils.Config;

public class GameView extends StackPane {

	private final GameScreen gameScreen;

	public GameView() {
		gameScreen = new GameScreen(Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);

		Pane overlay = new Pane(); // This allows absolute positioning
		overlay.setPickOnBounds(false);
		InputHandler inputHandler = gameScreen.getInputHandler();
		Toolbar toolbar = new Toolbar(gameScreen.getInputHandler(), overlay);
		inputHandler.setToolbar(toolbar);

		toolbar.setPickOnBounds(true);
		overlay.getChildren().add(toolbar);

		toolbar.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
			double centerX = (Config.SCREEN_WIDTH - newVal.getWidth()) / 2;
			toolbar.setLayoutX(centerX);
		});

		this.getChildren().addAll(gameScreen, overlay);
	}

	public void requestGameScreenFocus() {
		gameScreen.requestFocus();
	}
}
