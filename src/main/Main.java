package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;

public class Main extends Application {

	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int TILE_SIZE = 32;

	private GameLoop gameLoop;

	@Override
	public void start(Stage stage) {
		Pane root = new Pane();
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("DNA Factory");
		stage.show();

		gameLoop = new GameLoop(gc);
		gameLoop.start();

		// Handle mouse click to place an entity
		scene.setOnMouseClicked((MouseEvent e) -> {
			int gridX = (int) (e.getX() / TILE_SIZE);
			int gridY = (int) (e.getY() / TILE_SIZE);
			gameLoop.placeAt(gridX, gridY);
		});

		// Handle mouse movement to update ghost preview
		scene.setOnMouseMoved(e -> {
			gameLoop.updateMousePosition(e.getX(), e.getY());
		});

		// Handle key press to change build mode or rotate
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case R -> gameLoop.rotate();
			case E -> gameLoop.setBuildModeExtractor();
			case C -> gameLoop.setBuildModeConveyor();
			case D -> gameLoop.setBuildModeDNACombiner();
			case L -> gameLoop.setBuildModeLifeformAssembler();
			case X -> gameLoop.setBuildModeDelete();
			}
		});

	}

	public static void main(String[] args) {
		launch();
	}
}
