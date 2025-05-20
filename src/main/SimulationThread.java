package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import entities.Entity;
import grid.Grid;
import grid.Tile;
import items.Item;
import javafx.scene.canvas.GraphicsContext;
import logic.GoalManager;
import utils.Camera;
import utils.Game;
import utils.ItemMover;
import utils.SceneSwitcher;

public class SimulationThread {

	private static SimulationThread instance;

	private final AtomicReference<GameState> sharedState;
	private final ScheduledExecutorService executor;
	private final InputHandler inputHandler;
	private final GameState currentState;
	private final int tickRateNs;
	private boolean running = false;
	private GraphicsContext gc; // Graphics context for rendering

	private static final int TILE_SIZE = Tile.getTileSize(); // Size of each tile in pixels
	private static final int GRID_WIDTH = Grid.getGridWidth(); // Number of tiles in the grid
	private static final int GRID_HEIGHT = Grid.getGridHeight(); // Number of tiles in the grid

	private int tickCount = 0;
	private int tps = 0;
	private long lastTpsUpdateTime = System.nanoTime();

	public SimulationThread(InputHandler inputHandler, GameState initialState, AtomicReference<GameState> sharedState,
			int tickRateNs, GraphicsContext gc) {
		this.executor = Executors.newSingleThreadScheduledExecutor();
		this.inputHandler = inputHandler;
		this.currentState = initialState;
		this.sharedState = sharedState;
		this.tickRateNs = tickRateNs;
		this.gc = gc; // Initialize the graphics context
		instance = this; // Set the singleton instance
	}

	public static SimulationThread getInstance() {
		return instance;
	}

	public void start() {
		if (running)
			return;
		running = true;

		executor.scheduleAtFixedRate(() -> {
			try {
				tickCount++;
				long now = System.nanoTime();
				if (now - lastTpsUpdateTime >= 1000000000) { // 1 second
					tps = tickCount;
					tickCount = 0;
					lastTpsUpdateTime = now;
//					System.out.println("SimulationThread TPS: " + tps);

				}

				ArrayList<Entity> entities = currentState.getEntities();
				ArrayList<Entity> overlayEntities = currentState.getOverlayEntities();
				Item[][] testItems = currentState.getTestItems();
				Camera camera = currentState.getCamera();
				ItemMover itemMover = currentState.getItemMover();
				boolean hasEnded = currentState.isHasEnded();
				Grid grid = currentState.getGrid();

//				// === First pass: logic update ===
//				for (Entity e : entities) {
//					e.update(testItems);
////				System.out.println("Hello from entities");
//				}
//				for (Entity e : overlayEntities) {
//					e.update(testItems);
////				System.out.println("Hello from overlay entities");
//				}

				// === Second pass: item movement ===
				itemMover.begin(testItems);
				for (Entity e : entities) {
					e.update(testItems, itemMover);
//				System.out.println("Hello from entities with item mover");
				}
				for (Entity e : overlayEntities) {
					e.update(testItems, itemMover);
//				System.out.println("Hello from overlay entities with item mover");
				}
				itemMover.executeMoves();

				// Handle camera + input
				camera.update(inputHandler);
				placeAt(camera, grid, entities, overlayEntities);

				if (!hasEnded && GoalManager.getInstance().isGoalComplete()) {
					hasEnded = true;
					this.stop();
//				System.out.println("Simulation ended");
					javafx.application.Platform.runLater(() -> {
						SceneSwitcher.switchTo("/ui/EndPage.fxml", gc.getCanvas().getScene().getWindow());
					});
					executor.shutdownNow();
				}
				inputHandler.clearAllInputs();
				// Update the shared state
				sharedState.set(currentState.copy());
//			System.out.println("Hello from the simulation thread! ");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, 0, tickRateNs, TimeUnit.NANOSECONDS);
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
		executor.shutdownNow();
	}

	// Placement logic (called every frame)
	public void placeAt(Camera camera, Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities) {

		for (ArrayList<Double> pos : inputHandler.getClickPos()) {

			double clickX = pos.get(0) / camera.getZoom() + camera.getCameraX();
			double clickY = pos.get(1) / camera.getZoom() + camera.getCameraY();
//			System.out.println("Hello from the placeAt method1");
//			System.out.println("Click position: " + clickX + ", " + clickY);
			if (clickX < 0 || clickY < 0 || clickX >= GRID_WIDTH * TILE_SIZE || clickY >= GRID_HEIGHT * TILE_SIZE) {
//				System.out.println("Click out of bounds: " + clickX + ", " + clickY);
				continue;
			}
			int x = (int) (clickX / TILE_SIZE);
			int y = (int) (clickY / TILE_SIZE);
//			System.out.println("Grid position: " + x + ", " + y);

			inputHandler.getBuildMode().place(grid, entities, overlayEntities, x, y, inputHandler.getDirection());
//			System.out.println("Hello from the placeAt method2");
		}

	}

}
