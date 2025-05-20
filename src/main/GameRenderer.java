package main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import entities.Conveyor;
import entities.Conveyor.ConveyorType;
import entities.Entity;
import grid.Grid;
import grid.Tile;
import items.Item;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.GoalManager;
import utils.AssetManager;
import utils.Camera;
import utils.Config;
import utils.Direction;
import utils.Game;

public class GameRenderer extends AnimationTimer {

	private final AtomicReference<GameState> sharedState;
	private final GraphicsContext gc;
	private final Map<String, Image> itemImageCache = new HashMap<>();
	private static final int TILE_SIZE = Tile.getTileSize(); // Size of each tile in pixels
	private static final int GRID_WIDTH = Grid.getGridWidth(); // Number of tiles in the grid
	private static final int GRID_HEIGHT = Grid.getGridHeight(); // Number of tiles in the grid

	private static final int SCREENWIDTH = Config.SCREEN_WIDTH; // Fixed screen width
	private static final int SCREENHEIGHT = Config.SCREEN_WIDTH; // Fixed screen height

	private static final long REFRESH_RATE = getRefreshRate(); // 240 FPS
	private static final long FRAME_TIME = 1_000_000_000 / 60; // Time per frame in nanoseconds
	private long lastUpdateTime = 0; // Time of the last update in nanoseconds
	private long lastFpsUpdateTime = 0;
	private int frameCount = 0;
	private int fps = 0;

	public GameRenderer(AtomicReference<GameState> sharedState, GraphicsContext gc) {
		// TODO Auto-generated constructor stub
		this.sharedState = sharedState;
		this.gc = gc;
	}

	@Override
	public void handle(long arg0) {
		// Update FPS counter
		frameCount++;
		if (arg0 - lastFpsUpdateTime >= 1_000_000_000) { // 1 second
			fps = frameCount;
			frameCount = 0;
			lastFpsUpdateTime = arg0;
//            System.out.println("GameRenderer FPS: " + fps);
		}

		// Get camera position
		Item[][] testItems = sharedState.get().getTestItems();
		InputHandler inputHandler = sharedState.get().getInputHandler();
		ArrayList<Entity> entities = sharedState.get().getEntities();
		ArrayList<Entity> overlayEntities = sharedState.get().getOverlayEntities();
		Grid grid = sharedState.get().getGrid();
		Camera camera = sharedState.get().getCamera();
		double cameraX = camera.getCameraX();
		double cameraY = camera.getCameraY();
		double screenWidth = camera.getScreenWidth();
		double screenHeight = camera.getScreenHeight();
		double zoom = camera.getZoom();
//		System.out.println(zoom);

		// get mouse position relative to camera
		gc.clearRect(0, 0, SCREENWIDTH, SCREENHEIGHT); // Clear the canvas
		gc.save(); // Save the current state of the GraphicsContext
		gc.scale(zoom, zoom); // Apply zoom scaling
		gc.translate(-cameraX, -cameraY); // Apply camera translation

		// Draw grid
		grid.drawGrid(gc, camera);

		// Draw entities
		for (Entity e : entities) {
			e.render(gc, TILE_SIZE);
		}

		// Draw items (nucleotides, traits, lifeforms)
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				Item item = testItems[x][y];
				if (item != null) {
//							System.out.println("Rendering item at " + x + ", " + y + " with DNA code: " + item.getDnaCode());
					int padding = 1;
					Image icon = getItemImage(item);
					Entity entity = Game.instance.getEntityAt(x, y);
					if (entity instanceof Conveyor) {
						Conveyor conveyor = (Conveyor) entity;

						double[] interpolatedPos = getInterpolatedPosition(x, y, item.getProgress(), conveyor);

						gc.drawImage(icon, interpolatedPos[0] * TILE_SIZE + padding,
								interpolatedPos[1] * TILE_SIZE + padding, TILE_SIZE - 2 * padding,
								TILE_SIZE - 2 * padding);

					} else {

						gc.drawImage(icon, x * TILE_SIZE + padding, y * TILE_SIZE + padding, TILE_SIZE - 2 * padding,
								TILE_SIZE - 2 * padding);
					}

				}
			}
		}

		for (Entity e : overlayEntities) {
			e.render(gc, TILE_SIZE);
		}

		// Draw ghost preview
		grid.drawGhostPreview(gc, camera, inputHandler);

		// Reset the GraphicsContext
		gc.restore();

		// Render UI elements (not affected by the camera)
		GoalManager gm = GoalManager.getInstance();
		gc.setFill(Color.BLACK);
		gc.fillText("Stage " + gm.getCurrentStage() + "/" + gm.getTotalStages() + ": " + gm.getTargetLifeform() + " "
				+ gm.getDelivered() + "/" + gm.getGoalAmount(), 20, 20);

		gc.setFill(Color.DARKGRAY);
		gc.fillText("Current Tool: " + inputHandler.getBuildMode().getBuildModeLabel(), 20, 40);
		gc.fillText("Direction: " + inputHandler.getDirectionLabel() + " (Press R to rotate)", 20, 60);
		gc.fillText("Click to place", 20, 80);

	}

	private Image getItemImage(Item item) {
		// Use cached image if already loaded
		if (!itemImageCache.containsKey(item.getDnaCode().toLowerCase())) {
			switch (item.getDnaCode().toLowerCase()) {
			case "a" -> itemImageCache.put("a", AssetManager.nucleotideA);
			case "t" -> itemImageCache.put("t", AssetManager.nucleotideT);
			case "g" -> itemImageCache.put("g", AssetManager.nucleotideG);
			case "c" -> itemImageCache.put("c", AssetManager.nucleotideC);
			case "muscle" -> itemImageCache.put("muscle", AssetManager.traitMuscle);
			case "brain" -> itemImageCache.put("brain", AssetManager.traitBrain);
			case "blood" -> itemImageCache.put("blood", AssetManager.traitBlood);
			case "human" -> itemImageCache.put("human", AssetManager.lifeformHuman);
			case "octopus" -> itemImageCache.put("octopus", AssetManager.lifeformOctopus);
			case "worm" -> itemImageCache.put("worm", AssetManager.lifeformWorm);
			case "enzyme" -> itemImageCache.put("enzyme", AssetManager.enzyme);
			case "antibody" -> itemImageCache.put("antibody", AssetManager.antibody);
			case "organ_brain" -> itemImageCache.put("organ_brain", AssetManager.organBrain);
			case "organ_heart" -> itemImageCache.put("organ_heart", AssetManager.organHeart);
			case "organ_lungs" -> itemImageCache.put("organ_lungs", AssetManager.organLungs);
			}
			;
		}
		return itemImageCache.get(item.getDnaCode().toLowerCase());
	}

	public static int getRefreshRate() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int refreshRate = gd.getDisplayMode().getRefreshRate();
		return (refreshRate > 0) ? refreshRate : 60; // Default to 60 Hz if unknown
	}

	public double[] getInterpolatedPosition(int x, int y, double progress, Conveyor conveyor) {
		Direction direction = conveyor.getDirection();
		ConveyorType type = conveyor.getType();
		if (type == ConveyorType.STRAIGHT) {
			switch (direction) {
			case UP -> {
				return new double[] { x, y - progress + 0.5 };
			}
			case DOWN -> {
				return new double[] { x, y + progress - 0.5 };
			}
			case LEFT -> {
				return new double[] { x - progress + 0.5, y };
			}
			case RIGHT -> {
				return new double[] { x + progress - 0.5, y };
			}
			default -> {
				return new double[] { x, y };
			}
			}

		} else if (type == ConveyorType.CURVE_BOTTOM_TO_LEFT) {
			return new double[] { x - 0.5 + 0.5 * Math.cos(Math.PI / 2 * progress),
					y + 0.5 - 0.5 * Math.sin(Math.PI / 2 * progress) };
		} else if (type == ConveyorType.CURVE_BOTTOM_TO_RIGHT) {
			return new double[] { x + 0.5 - 0.5 * Math.cos(Math.PI / 2 * progress),
					y + 0.5 - 0.5 * Math.sin(Math.PI / 2 * progress) };
		} else if (type == ConveyorType.CURVE_LEFT_TO_BOTTOM) {
			return new double[] { x - 0.5 + 0.5 * Math.sin(Math.PI / 2 * progress),
					y + 0.5 - 0.5 * Math.cos(Math.PI / 2 * progress) };
		} else if (type == ConveyorType.CURVE_LEFT_TO_TOP) {
			return new double[] { x - 0.5 + 0.5 * Math.sin(Math.PI / 2 * progress),
					y - 0.5 + 0.5 * Math.cos(Math.PI / 2 * progress) };
		} else if (type == ConveyorType.CURVE_RIGHT_TO_BOTTOM) {
			return new double[] { x + 0.5 - 0.5 * Math.sin(Math.PI / 2 * progress),
					y + 0.5 - 0.5 * Math.cos(Math.PI / 2 * progress) };
		} else if (type == ConveyorType.CURVE_RIGHT_TO_TOP) {
			return new double[] { x + 0.5 - 0.5 * Math.sin(Math.PI / 2 * progress),
					y - 0.5 + 0.5 * Math.cos(Math.PI / 2 * progress) };
		} else if (type == ConveyorType.CURVE_TOP_TO_LEFT) {
			return new double[] { x - 0.5 + 0.5 * Math.cos(Math.PI / 2 * progress),
					y - 0.5 + 0.5 * Math.sin(Math.PI / 2 * progress) };
		} else {
			return new double[] { x + 0.5 - 0.5 * Math.cos(Math.PI / 2 * progress),
					y - 0.5 + 0.5 * Math.sin(Math.PI / 2 * progress) };
		}

	}

	private double getAngle(Direction direction) {
		return switch (direction) {
		case RIGHT -> 0;
		case DOWN -> 90;
		case LEFT -> 180;
		case UP -> 270;
		default -> 0;
		};

	}

}
