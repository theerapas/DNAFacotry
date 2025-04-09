package main;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.GoalManager;
import items.Item;
import items.NucleotideFactory;
import entities.Conveyor;
import entities.DNACombiner;
import entities.DeliveryZone;
import entities.Entity;
import entities.Extractor;
import entities.LifeformAssembler;
import utils.Direction;
import grid.Tile;

import java.util.ArrayList;

public class GameLoop extends AnimationTimer {

	private enum BuildMode {
		CONVEYOR, EXTRACTOR, DNA_COMBINER, LIFEFORM_ASSEMBLER, DELETE
	}

	private BuildMode buildMode = BuildMode.CONVEYOR;
	private Direction currentDirection = Direction.RIGHT;

	private static final int TILE_SIZE = 32;
	private static final int GRID_WIDTH = 20;
	private static final int GRID_HEIGHT = 15;

	private Item[][] testItems = new Item[GRID_WIDTH][GRID_HEIGHT];
	private GraphicsContext gc;
	private ArrayList<Entity> entities = new ArrayList<>();

	private Tile[][] tileMap = new Tile[GRID_WIDTH][GRID_HEIGHT];

	// Mouse position for ghost preview
	private int mouseX = -1;
	private int mouseY = -1;

	public GameLoop(GraphicsContext gc) {
		this.gc = gc;

		// === Initialize tile map ===
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				tileMap[x][y] = new Tile(x, y);
			}
		}

		// === Place some resource tiles ===
		tileMap[4][4].setResource("A");
		tileMap[6][4].setResource("T");
		tileMap[4][6].setResource("G");
		tileMap[6][6].setResource("C");

		// === Add goal delivery hub ===
		entities.add(new DeliveryZone(16, 7));

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

		// === Draw resource tiles (background + letter) ===
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				if (tileMap[x][y].isResource()) {
					gc.setFill(Color.BEIGE);
					gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
					gc.setFill(Color.DARKBLUE);
					gc.fillText(tileMap[x][y].getResource(), x * TILE_SIZE + 10, y * TILE_SIZE + 20);
				}
			}
		}

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

			// Preview icon based on build mode and direction
			String preview = switch (buildMode) {
			case CONVEYOR -> switch (currentDirection) {
			case UP -> "↑";
			case RIGHT -> "→";
			case DOWN -> "↓";
			case LEFT -> "←";
			};
			case EXTRACTOR -> "E";
			case DNA_COMBINER -> "⚗";
			case LIFEFORM_ASSEMBLER -> "🧬";
			case DELETE -> "❌";
			};

			gc.setFill(Color.BLACK);
			gc.fillText(preview, mouseX * TILE_SIZE + 10, mouseY * TILE_SIZE + 20);
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

		GoalManager gm = GoalManager.getInstance();
		gc.setFill(Color.BLACK);
		gc.fillText("Goal: " + gm.getTargetLifeform() + " " + gm.getDelivered() + "/" + gm.getGoalAmount(), 20, 20);

		gc.setFill(Color.DARKGRAY);
		gc.fillText("Current Tool: " + getBuildModeLabel(), 20, 40);
		gc.fillText("Direction: " + getDirectionLabel() + " (Press R to rotate)", 20, 60);
		gc.fillText("Click to place", 20, 80);
		gc.fillText("C : Conveyor, E : Extractor, D : DNA Combiner, L : Lifeform Combiner, X : Delete", 20, 100);

	}

	// Mouse movement tracking (called from Main.java)
	public void updateMousePosition(double sceneX, double sceneY) {
		this.mouseX = (int) (sceneX / TILE_SIZE);
		this.mouseY = (int) (sceneY / TILE_SIZE);
	}

	// Placement logic (called on click)
	public void placeAt(int x, int y) {
		if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT)
			return;

		switch (buildMode) {
		case CONVEYOR -> entities.add(new Conveyor(x, y, currentDirection));

		case EXTRACTOR -> {
			Tile tile = tileMap[x][y];
			if (tile.isResource()) {
				String code = tile.getResource();
				entities.add(new Extractor(x, y, currentDirection, code));
				System.out.println("Placed extractor for " + code + " at (" + x + "," + y + ")");
			} else {
				System.out.println("Cannot place extractor — not a resource tile!");
			}
		}

		case DNA_COMBINER -> entities.add(new DNACombiner(x, y, currentDirection));

		case LIFEFORM_ASSEMBLER -> entities.add(new LifeformAssembler(x, y, currentDirection));

		case DELETE -> {
			entities.removeIf(e -> e.getX() == x && e.getY() == y && !(e instanceof entities.DeliveryZone));
			System.out.println("Removed entity at (" + x + "," + y + ")");
		}

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

	public void setBuildModeDNACombiner() {
		buildMode = BuildMode.DNA_COMBINER;
		System.out.println("Build Mode: DNA Combiner");
	}

	public void setBuildModeLifeformAssembler() {
		buildMode = BuildMode.LIFEFORM_ASSEMBLER;
		System.out.println("Build Mode: Lifeform Assembler");
	}

	private String getBuildModeLabel() {
		return switch (buildMode) {
		case EXTRACTOR -> "Extractor (E)";
		case CONVEYOR -> "Conveyor (C)";
		case DNA_COMBINER -> "DNA Combiner (D)";
		case LIFEFORM_ASSEMBLER -> "Lifeform Assembler (L)";
		case DELETE -> "Delete (X)";
		};
	}

	private String getDirectionLabel() {
		return switch (currentDirection) {
		case UP -> "↑ UP";
		case RIGHT -> "→ RIGHT";
		case DOWN -> "↓ DOWN";
		case LEFT -> "← LEFT";
		};
	}

	public void setBuildModeDelete() {
		buildMode = BuildMode.DELETE;
		System.out.println("Build Mode: Delete");
	}

}
