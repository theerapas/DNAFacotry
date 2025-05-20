package ui;

import buildMode.*;
import entities.DNASynthesizer;
import entities.Ribosome;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import main.InputHandler;
import utils.AssetManager;
import utils.Config;

public class Toolbar extends HBox {
	private double dragOffsetX;
	private double dragOffsetY;
	private FloatingToolbar dnaSynthesizerSubmenu;
	private FloatingToolbar ribosomeSubmenu;
	private final Pane root;

	public Toolbar(InputHandler inputHandler, Pane root) {
		this.root = root;

		this.setSpacing(4);
		this.setPadding(new Insets(6));
		this.setStyle("-fx-background-color: rgba(255,255,255,0.6); -fx-border-color: #aaa; -fx-border-width: 1;");
		this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		addDragHandle();

		addIconButton("conveyor.png", new ConveyorBuildMode(), inputHandler);
		addIconButton("tunnel.png", new TunnelBuildMode(), inputHandler);
		addIconButton("extractor_a.png", new ExtractorBuildMode(), inputHandler);
		addIconButton("dna_synthesizer1_icon.png", new DNASynthesizerBuildMode(), inputHandler, true);
		addIconButton("ribosome1.png", new RibosomeBuildMode(), inputHandler, true);
		addIconButton("organ_synthesizer_icon.png", new OrganSynthesizerBuildMode(), inputHandler);
		addIconButton("lifeform_assembler_icon.png", new LifeformAssemblerBuildMode(), inputHandler);
		addIconButton("delete.png", new DeleteBuildMode(), inputHandler);

		initializeSubmenus(inputHandler);

		root.getChildren().addAll(dnaSynthesizerSubmenu, ribosomeSubmenu);
	}

	private void initializeSubmenus(InputHandler inputHandler) {
		dnaSynthesizerSubmenu = new FloatingToolbar();
		dnaSynthesizerSubmenu.addIconButton("dna_synthesizer1_icon.png", () -> {
			var mode = new DNASynthesizerBuildMode();
			mode.setMode(DNASynthesizer.Mode.MODE1);
			inputHandler.setBuildMode(mode);
		});
		dnaSynthesizerSubmenu.addIconButton("dna_synthesizer2_icon.png", () -> {
			var mode = new DNASynthesizerBuildMode();
			mode.setMode(DNASynthesizer.Mode.MODE2);
			inputHandler.setBuildMode(mode);
		});
		dnaSynthesizerSubmenu.addIconButton("dna_synthesizer3_icon.png", () -> {
			var mode = new DNASynthesizerBuildMode();
			mode.setMode(DNASynthesizer.Mode.MODE3);
			inputHandler.setBuildMode(mode);
		});

		ribosomeSubmenu = new FloatingToolbar();
		ribosomeSubmenu.addIconButton("ribosome1.png", () -> {
			var mode = new RibosomeBuildMode();
			mode.setMode(Ribosome.Mode.RIBOSOME1);
			inputHandler.setBuildMode(mode);
		});
		ribosomeSubmenu.addIconButton("ribosome2.png", () -> {
			var mode = new RibosomeBuildMode();
			mode.setMode(Ribosome.Mode.RIBOSOME2);
			inputHandler.setBuildMode(mode);
		});
	}

	public void hideAllSubmenus() {
		dnaSynthesizerSubmenu.setVisible(false);
		ribosomeSubmenu.setVisible(false);
	}

	private static final ClassLoader classLoader = AssetManager.class.getClassLoader();

	private void addIconButton(String iconFilename, BuildMode mode, InputHandler inputHandler) {
		addIconButton(iconFilename, mode, inputHandler, false);
	}

	private void addIconButton(String iconFilename, BuildMode mode, InputHandler inputHandler, boolean hasSubmenu) {
		Image icon = new Image(classLoader.getResourceAsStream("assets/images/" + iconFilename), 32, 32, true, true);
		ImageView iconView = new ImageView(icon);
		Button button = new Button();
		button.setGraphic(iconView);
		button.getStyleClass().add("toolbar-icon-button");
		button.setOnAction(e -> {
			hideAllSubmenus();

			if (hasSubmenu) {
				Bounds buttonBounds = button.localToScene(button.getBoundsInLocal());
				Bounds rootBounds = root.sceneToLocal(buttonBounds);

				if (mode instanceof DNASynthesizerBuildMode dnaSynthesizerBuildMode) {
					dnaSynthesizerBuildMode.setMode(DNASynthesizer.Mode.MODE1);
					inputHandler.setBuildMode(dnaSynthesizerBuildMode);
					dnaSynthesizerSubmenu.setLayoutX(rootBounds.getMaxX());
					dnaSynthesizerSubmenu.setLayoutY(rootBounds.getMinY() + 52);
					dnaSynthesizerSubmenu.setVisible(true);
				} else if (mode instanceof RibosomeBuildMode ribosomeBuildMode) {
					ribosomeBuildMode.setMode(Ribosome.Mode.RIBOSOME1);
					inputHandler.setBuildMode(ribosomeBuildMode);
					ribosomeSubmenu.setLayoutX(rootBounds.getMaxX());
					ribosomeSubmenu.setLayoutY(rootBounds.getMinY() + 52);
					ribosomeSubmenu.setVisible(true);
				}
			} else {
				inputHandler.setBuildMode(mode);
			}
			getScene().lookup("#gameScreen").requestFocus();
		});
		this.getChildren().add(button);
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
}
