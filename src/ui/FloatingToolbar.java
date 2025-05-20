package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import utils.AssetManager;
import utils.Config;

public class FloatingToolbar extends HBox {
	private double dragOffsetX;
	private double dragOffsetY;
	private static final ClassLoader classLoader = AssetManager.class.getClassLoader();

	public FloatingToolbar() {
		this.setSpacing(4);
		this.setPadding(new Insets(6));
		this.setStyle("-fx-background-color: rgba(255,255,255,0.6); -fx-border-color: #aaa; -fx-border-width: 1;");
		this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		this.setVisible(false); // hide by default

		this.setPickOnBounds(true);

		addDragHandle();
	}

	private void addDragHandle() {
		Image dragIcon = new Image(classLoader.getResourceAsStream("assets/images/drag.png"), 32, 32, true, true);
		ImageView dragView = new ImageView(dragIcon);
		Button dragButton = new Button();
		dragButton.setGraphic(dragView);
		dragButton.setStyle("-fx-background-color: transparent;");
		dragButton.getStyleClass().add("toolbar-icon-button");

		dragButton.setOnMousePressed(e -> {
			dragOffsetX = e.getSceneX() - getLayoutX();
			dragOffsetY = e.getSceneY() - getLayoutY();
		});

		dragButton.setOnMouseDragged(this::handleDrag);

		dragButton.setOnMouseReleased(e -> {
			getScene().lookup("#gameScreen").requestFocus();
		});

		this.getChildren().add(dragButton);
	}

	private void handleDrag(MouseEvent e) {
		double newX = e.getSceneX() - dragOffsetX;
		double newY = e.getSceneY() - dragOffsetY;

		double maxX = Config.SCREEN_WIDTH - this.getWidth();
		double maxY = Config.SCREEN_HEIGHT - this.getHeight();

		newX = Math.max(0, Math.min(newX, maxX));
		newY = Math.max(0, Math.min(newY, maxY));

		setLayoutX(newX);
		setLayoutY(newY);
	}

	public void addIconButton(String iconFilename, Runnable action) {
		Image icon = new Image(classLoader.getResourceAsStream("assets/images/" + iconFilename), 32, 32, true, true);
		ImageView iconView = new ImageView(icon);
		Button button = new Button();
		button.setGraphic(iconView);
		button.getStyleClass().add("toolbar-icon-button");
		button.setOnAction(e -> {
			action.run();
			this.setVisible(false); // auto-close submenu
			getScene().lookup("#gameScreen").requestFocus();
		});
		this.getChildren().add(button);
	}
}
