package main;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import items.Item;
import items.NucleotideFactory;
import entities.Conveyor;
import entities.DNACombiner;
import entities.Entity;
import entities.Extractor;
import entities.LifeformAssembler;
import utils.Direction;

import java.util.ArrayList;

public class GameLoop extends AnimationTimer {

	private enum BuildMode {
		CONVEYOR, EXTRACTOR
	}

	private BuildMode buildMode = BuildMode.CONVEYOR;
	private Direction currentDirection = Direction.RIGHT;

	private static final int TILE_SIZE = 32;
	private static final int GRID_WIDTH = 20;
	private static final int GRID_HEIGHT = 15;

	private Item[][] testItems = new Item[GRID_WIDTH][GRID_HEIGHT];
	private GraphicsContext gc;
	private ArrayList<Entity> entities = new ArrayList<>();

	// Mouse position for ghost preview
	private int mouseX = -1;
	private int mouseY = -1;

	public GameLoop(GraphicsContext gc) {
		this.gc = gc;

		// Place manual traits for testing
		testItems[10][6] = new Item(Item.ItemType.TRAIT, "BLOOD", "#ff6666");
		testItems[10][8] = new Item(Item.ItemType.TRAIT, "MUSCLE", "#66cc66");

		// Top extractor pointing down (feeds into y=6)
	    entities.add(new Extractor(6, 5, Direction.DOWN));
	    entities.add(new Conveyor(6, 6, Direction.DOWN));

	    // Bottom extractor pointing up (feeds into y=8)
	    entities.add(new Extractor(6, 9, Direction.UP));
	    entities.add(new Conveyor(6, 8, Direction.UP));

	    // Combiner at (6, 7), input from top & bottom, output to right
	    entities.add(new DNACombiner(6, 7, Direction.RIGHT));

	    // Output belt to right
	    entities.add(new Conveyor(7, 7, Direction.RIGHT));

	    // Lifeform assembler — producing HUMAN from BLOOD + MUSCLE
	    entities.add(new LifeformAssembler(10, 7, Direction.RIGHT));
	    entities.add(new Conveyor(11, 7, Direction.RIGHT));
	}

	@Override
	public void handle(long now) {
		update();
		render();
	}

	private void update() {
		for (Entity e : entities) {
			e.update(testItems);
		}
	}

	private void render() {
		gc.clearRect(0, 0, 1280, 720);

		// Draw grid
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				gc.setStroke(Color.LIGHTGRAY);
				gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}

		// Draw ghost preview
		if (mouseX >= 0 && mouseY >= 0 && mouseX < GRID_WIDTH && mouseY < GRID_HEIGHT) {
			gc.setGlobalAlpha(0.5);
			gc.setFill(Color.LIGHTBLUE);
			gc.fillRect(mouseX * TILE_SIZE, mouseY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			gc.setGlobalAlpha(1.0);
		}

		// Draw entities
		for (Entity e : entities) {
			e.render(gc, TILE_SIZE);
		}

		// Draw items (nucleotides, traits, lifeforms)
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				Item item = testItems[x][y];
				if (item != null) {
					gc.setFill(Color.web(item.getColor()));
					gc.fillText(item.getDnaCode(), x * TILE_SIZE + 10, y * TILE_SIZE + 20);
				}
			}
		}
	}

	// Mouse movement tracking (called from Main.java)
	public void updateMousePosition(double sceneX, double sceneY) {
		this.mouseX = (int) (sceneX / TILE_SIZE);
		this.mouseY = (int) (sceneY / TILE_SIZE);
	}

	// Placement logic (called on click)
	public void placeAt(int x, int y) {
		switch (buildMode) {
			case CONVEYOR -> entities.add(new Conveyor(x, y, currentDirection));
			case EXTRACTOR -> entities.add(new Extractor(x, y, currentDirection));
		}
	}

	// Rotation and build mode switching (called on keypress)
	public void rotate() {
		currentDirection = currentDirection.rotateClockwise();
	}

	public void setBuildModeExtractor() {
		buildMode = BuildMode.EXTRACTOR;
		System.out.println("Build Mode: Extractor");
	}

	public void setBuildModeConveyor() {
		buildMode = BuildMode.CONVEYOR;
		System.out.println("Build Mode: Conveyor");
	}
}
