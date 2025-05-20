package ui;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.GameScreen;
import main.GameView;
import utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class StartPageController {

	@FXML
	private StackPane rootPane;
	@FXML
	private VBox rootVBox;
	@FXML
	private Canvas particleCanvas;
	@FXML
	private Button startButton;

	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;

	private List<Particle> particles = new ArrayList<>();
	private AnimationTimer particleTimer;

	@FXML
	public void initialize() {
		SoundManager.playBackgroundMusic("StartMenu.mp3", true);
		initParticles();
		startParticleAnimation();
		javafx.application.Platform.runLater(() -> {
			rootPane.getScene().getStylesheets()
					.add(getClass().getResource("/assets/styles/style.css").toExternalForm());
		});
		startButton.setOnMouseEntered(e -> startButton.getStyleClass().add("start-button-hover"));
		startButton.setOnMouseExited(e -> startButton.getStyleClass().remove("start-button-hover"));
	}

	@FXML
	private void handleStart(ActionEvent event) {
		SoundManager.stopBackgroundMusic();

		FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), rootVBox);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);

		fadeOut.setOnFinished(e -> {
			Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

			GameView gameView = new GameView();
			gameView.setFocusTraversable(true);
			stage.getScene().setRoot(gameView);

			// ✅ Ensure focus is given after layout/render is ready
			Platform.runLater(gameView::requestGameScreenFocus);

			gameView.setOpacity(0);
			FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), gameView);
			fadeIn.setFromValue(0.0);
			fadeIn.setToValue(1.0);
			fadeIn.play();

			stopParticleAnimation();
		});

		fadeOut.play();
	}

	private void initParticles() {
		for (int i = 0; i < 100; i++) {
			particles.add(new Particle());
		}
	}

	private void startParticleAnimation() {
		GraphicsContext gc = particleCanvas.getGraphicsContext2D();
		particleTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				gc.clearRect(0, 0, WIDTH, HEIGHT);
				for (Particle p : particles) {
					p.update();
					p.draw(gc);
				}
			}
		};
		particleTimer.start();
	}

	private void stopParticleAnimation() {
		if (particleTimer != null) {
			particleTimer.stop();
		}
	}

	private static class Particle {
		private static final Color[] COLORS = new Color[] { Color.rgb(255, 235, 59, 0.4), // Yellow
				Color.rgb(156, 39, 176, 0.4), // Purple
				Color.rgb(76, 175, 80, 0.4), // Green
				Color.rgb(0, 188, 212, 0.4) // Cyan
		};

		double x = Math.random() * WIDTH;
		double y = Math.random() * HEIGHT;
		double dx = (Math.random() - 0.5) * 0.3;
		double dy = (Math.random() - 0.5) * 0.3;
		double radius = 1 + Math.random() * 2;
		Color color = COLORS[(int) (Math.random() * COLORS.length)];

		void update() {
			x += dx;
			y += dy;
			if (x < 0 || x > WIDTH)
				dx *= -1;
			if (y < 0 || y > HEIGHT)
				dy *= -1;
		}

		void draw(GraphicsContext gc) {
			gc.setFill(color);
			gc.fillOval(x, y, radius * 2, radius * 2);
		}
	}

}
