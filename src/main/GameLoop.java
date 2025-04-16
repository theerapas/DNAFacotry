package main;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
import entities.Tunnel;
import entities.TunnelEnd;
import utils.Direction;
import utils.Game;
import grid.Tile;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;

import static main.Config.*;

public class GameLoop extends AnimationTimer {

	private enum BuildMode {
		CONVEYOR, EXTRACTOR, DNA_COMBINER, LIFEFORM_ASSEMBLER, TUNNEL, DELETE
	}

	private BuildMode buildMode = BuildMode.CONVEYOR;
	private Direction currentDirection = Direction.RIGHT;

	private Item[][] testItems = new Item[GRID_WIDTH][GRID_HEIGHT];
	private GraphicsContext gc;
	private ArrayList<Entity> entities = new ArrayList<>();
	private ArrayList<Entity> overlayEntities = new ArrayList<>();

	private Tile[][] tileMap = new Tile[GRID_WIDTH][GRID_HEIGHT];

	private final Map<String, Image> itemImageCache = new HashMap<>();
	private final Map<String, Image> resourceImageCache = new HashMap<>();

	private Tunnel pendingTunnelStart = null;

	private int mouseX = -1;
	private int mouseY = -1;

	public GameLoop(GraphicsContext gc) {
		this.gc = gc;
		Game.instance = this;

		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				tileMap[x][y] = new Tile(x, y);
			}
		}

		tileMap[4][4].setResource("A");
		tileMap[6][4].setResource("T");
		tileMap[4][6].setResource("G");
		tileMap[6][6].setResource("C");

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
		for (Entity e : overlayEntities) {
			e.update(testItems);
		}
	}

	private void render() {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				if (tileMap[x][y].isResource()) {
					String code = tileMap[x][y].getResource();
					String key = "resource_" + code.toLowerCase();
					if (!resourceImageCache.containsKey(key)) {
						Image img = new Image(ClassLoader.getSystemResourceAsStream("assets/" + key + ".png"));
						resourceImageCache.put(key, img);
					}
					gc.drawImage(resourceImageCache.get(key), x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				}
			}
		}

		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				gc.setStroke(Color.LIGHTGRAY);
				gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}

		if (mouseX >= 0 && mouseY >= 0 && mouseX < GRID_WIDTH && mouseY < GRID_HEIGHT) {
			gc.setGlobalAlpha(0.5);
			gc.setFill(Color.LIGHTBLUE);
			gc.fillRect(mouseX * TILE_SIZE, mouseY * TILE_SIZE, TILE_SIZE, TILE_SIZE);

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
				case TUNNEL -> "T";
				case DELETE -> "❌";
			};

			gc.setFill(Color.BLACK);
			gc.fillText(preview, mouseX * TILE_SIZE + 10, mouseY * TILE_SIZE + 20);
			gc.setGlobalAlpha(1.0);
		}

		for (Entity e : entities) {
			e.render(gc, TILE_SIZE);
		}
		for (Entity e : overlayEntities) {
			e.render(gc, TILE_SIZE);
		}

		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				Item item = testItems[x][y];
				if (item != null) {
					Image icon = getItemImage(item);
					int padding = 8;
					gc.drawImage(icon,
						x * TILE_SIZE + padding,
						y * TILE_SIZE + padding,
						TILE_SIZE - 2 * padding,
						TILE_SIZE - 2 * padding
					);
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
		gc.fillText("C : Conveyor, E : Extractor, D : DNA Combiner, L : Lifeform Combiner,T : Tunnel, X : Delete", 20,
				100);
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
		case CONVEYOR -> {
			var conveyor = new Conveyor(x, y, currentDirection);
			if (!canPlaceEntityAt(x, y, conveyor))
				return;
			entities.add(conveyor);
		}

		case EXTRACTOR -> {
			Tile tile = tileMap[x][y];
			if (tile.isResource()) {
				String code = tile.getResource();
				var extractor = new Extractor(x, y, currentDirection, code);
				if (!canPlaceEntityAt(x, y, extractor))
					return;
				entities.add(extractor);
				System.out.println("Placed extractor for " + code + " at (" + x + "," + y + ")");
			} else {
				System.out.println("Cannot place extractor — not a resource tile!");
			}
		}

		case DNA_COMBINER -> {
			var combiner = new DNACombiner(x, y, currentDirection);
			if (!canPlaceEntityAt(x, y, combiner))
				return;
			entities.add(combiner);
		}

		case LIFEFORM_ASSEMBLER -> {
			var assembler = new LifeformAssembler(x, y, currentDirection);
			if (!canPlaceEntityAt(x, y, assembler))
				return;
			entities.add(assembler);
		}

		case TUNNEL -> {
			if (pendingTunnelStart == null) {
				// Step 1: Place entrance
				var tunnel = new Tunnel(x, y, currentDirection, -1, -1);
				if (!canPlaceEntityAt(x, y, tunnel)) return;

				pendingTunnelStart = tunnel;
				overlayEntities.add(tunnel); // Tunnel on overlay
				entities.add(new Conveyor(x, y, currentDirection)); // Conveyor on base
				System.out.println("Tunnel start placed at (" + x + "," + y + ")");
			} else {
				// Step 2: Place exit
				var tunnelEnd = new TunnelEnd(x, y, currentDirection);
				if (!canPlaceEntityAt(x, y, tunnelEnd)) return;

				pendingTunnelStart.setExit(x, y);
				overlayEntities.add(tunnelEnd); // Tunnel end on overlay
				entities.add(new Conveyor(x, y, currentDirection)); // Conveyor on base
				System.out.println("Tunnel exit placed at (" + x + "," + y + ")");
				pendingTunnelStart = null;
			}
		}

		case DELETE -> {
			// Remove overlay entities (like Tunnel/TunnelEnd)
			overlayEntities.removeIf(e -> {
				if (e.getX() == x && e.getY() == y) {
					if (e instanceof TunnelEnd) {
						// Clear the exit from any tunnel that points here
						for (Entity base : overlayEntities) {
							if (base instanceof Tunnel tunnel) {
								tunnel.clearExitIfMatches(x, y);
							}
						}
					}
					return true;
				}
				return false;
			});

			// Remove base entities (except DeliveryZone)
			entities.removeIf(e -> e.getX() == x && e.getY() == y && !(e instanceof DeliveryZone));

			// Clear any item on tile too
			testItems[x][y] = null;

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
		case TUNNEL -> "Tunnel (T)";
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
	
	public Entity getEntityAt(int x, int y) {
		for (Entity e : entities) {
			if (e.getX() == x && e.getY() == y)
				return e;
		}
		for (Entity e : overlayEntities) {
			if (e.getX() == x && e.getY() == y)
				return e;
		}
		return null;
	}


	public void setBuildModeDelete() {
		buildMode = BuildMode.DELETE;
		System.out.println("Build Mode: Delete");
	}

	public void setBuildModeTunnel() {
		buildMode = BuildMode.TUNNEL;
		System.out.println("Build Mode: Tunnel");
	}

	private Image getItemImage(Item item) {
		String name = switch (item.getType()) {
		case NUCLEOTIDE -> "nucleotide_" + item.getDnaCode().toLowerCase();
		case TRAIT -> "trait_" + item.getDnaCode().toLowerCase();
		case LIFEFORM -> "lifeform_" + item.getDnaCode().toLowerCase();
		};

		// Use cached image if already loaded
		if (!itemImageCache.containsKey(name)) {
			Image img = new Image(ClassLoader.getSystemResourceAsStream("assets/" + name + ".png"));
			itemImageCache.put(name, img);
		}
		return itemImageCache.get(name);
	}

	private boolean canPlaceEntityAt(int x, int y, Entity newEntity) {
		// Check tile bounds
		if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT)
			return false;

		// Check delivery zone
		for (Entity e : entities) {
			if (e instanceof DeliveryZone && e.getX() == x && e.getY() == y)
				return false;
		}

		// Only extractor can be placed on resource tile
		if (tileMap[x][y].isResource() && !(newEntity instanceof Extractor))
			return false;

		// Check in entities
		for (Entity e : entities) {
			if (e.getX() == x && e.getY() == y) {
				boolean tunnelBeltCombo = ((e instanceof Conveyor
						&& (newEntity instanceof Tunnel || newEntity instanceof TunnelEnd))
						|| ((e instanceof Tunnel || e instanceof TunnelEnd) && newEntity instanceof Conveyor));
				if (!tunnelBeltCombo)
					return false;
			}
		}

		// Check in overlayEntities
		for (Entity e : overlayEntities) {
			if (e.getX() == x && e.getY() == y) {
				boolean tunnelBeltCombo = ((e instanceof Conveyor
						&& (newEntity instanceof Tunnel || newEntity instanceof TunnelEnd))
						|| ((e instanceof Tunnel || e instanceof TunnelEnd) && newEntity instanceof Conveyor));
				if (!tunnelBeltCombo)
					return false;
			}
		}

		return true;
	}

}
