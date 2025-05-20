package main;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import utils.Camera;
import utils.Config;
import utils.Direction;
import utils.Game;
import utils.ItemMover;
import utils.MapGenerator;
import utils.SoundManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import entities.Conveyor;
import entities.DeliveryZone;
import entities.Entity;
import grid.Grid;
import grid.Tile;
import items.Item;
import javafx.scene.input.MouseButton;

public class GameScreen extends Canvas {

	private static final int TILE_SIZE = Tile.getTileSize(); // Size of each tile in pixels
	private static final int GRID_WIDTH = Grid.getGridWidth(); // Number of tiles in the grid
	private static final int GRID_HEIGHT = Grid.getGridHeight(); // Number of tiles in the grid
//	private GameLoop gameLoop;
	private InputHandler inputHandler = new InputHandler();
	private GameState currentGameState; // Initialize with a default state
	private static final int TICK_RATE_NS = 1000000000 / 240; // 240 TPS
	private AtomicReference<GameState> sharedState;

	public GameScreen(int width, int height) {
		super(width, height);
		ArrayList<Entity> entities = new ArrayList<>();
		ArrayList<Entity> overlayEntities = new ArrayList<>();
		GraphicsContext gc = this.getGraphicsContext2D();
		addListener();
		Item[][] testItems = new Item[GRID_WIDTH][GRID_HEIGHT];
		Camera camera = new Camera((GRID_WIDTH * TILE_SIZE - Config.SCREEN_WIDTH) / 2.0,
				(GRID_HEIGHT * TILE_SIZE - Config.SCREEN_HEIGHT) / 2.0, 4, inputHandler,1);
		Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);
		// === Resource tiles ===
		MapGenerator mapGenerator = new MapGenerator(GRID_WIDTH, GRID_HEIGHT, System.currentTimeMillis());
		mapGenerator.generateResources(grid.getTiles());
		// === Add delivery hub ===
		overlayEntities.add(new DeliveryZone(47, 47));

		SoundManager.playBackgroundMusic("DNAMusic.mp3", true);

		currentGameState = new GameState(testItems, entities, overlayEntities, grid, inputHandler, new ItemMover(),
				camera);

		Game.instance = currentGameState; // Initialize Game.instance
		sharedState = new AtomicReference<>(currentGameState);
		SimulationThread simulationThread = new SimulationThread(inputHandler, currentGameState, sharedState,
				TICK_RATE_NS, gc);
		GameRenderer gameRenderer = new GameRenderer(sharedState, gc);
		simulationThread.start();
		gameRenderer.start();

		this.setOnMousePressed(e -> this.requestFocus()); // For not focus on toolbar
		this.setId("gameScreen");
	}

	public void addListener() {

		this.setOnMouseEntered((MouseEvent e) -> {
			inputHandler.setMouseOnScreen(true); // Mouse is on the screen
		});

		this.setOnMouseExited((MouseEvent e) -> {
			inputHandler.setMouseOnScreen(false); // Mouse is off the screen
		});
		this.setOnMouseClicked((MouseEvent e) -> {
			inputHandler.setMouseX(e.getX());
			inputHandler.setMouseY(e.getY());
			inputHandler.addClickPos(e.getX(), e.getY());
			inputHandler.setMouseButtonPressed(e.getButton(), true);
		});

		this.setOnMouseReleased((MouseEvent e) -> {
			inputHandler.setMouseButtonPressed(e.getButton(), false);
		});

		this.setOnMouseMoved((MouseEvent e) -> {
			inputHandler.setMouseX(e.getX());
			inputHandler.setMouseY(e.getY());
		});

		this.setOnKeyPressed((KeyEvent e) -> {
			inputHandler.setKeyPressed(e.getCode(), true);
		});

		this.setOnKeyReleased((KeyEvent e) -> {
			inputHandler.setKeyPressed(e.getCode(), false);
		});

		this.setOnMouseDragged((MouseEvent e) -> {
			// Handle mouse drag event
			// implement later
		});

		this.setOnScroll(event -> inputHandler.handleScroll(event));
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}
}
