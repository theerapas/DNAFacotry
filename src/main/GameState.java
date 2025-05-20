package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import entities.Conveyor;
import entities.DeliveryZone;
import entities.Entity;
import entities.Extractor;
import entities.Tunnel;
import entities.TunnelEnd;
import grid.Grid;
import grid.Tile;
import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.GoalManager;
import utils.Camera;
import utils.ItemMover;
import utils.SceneSwitcher;

public class GameState {

	private static final int TILE_SIZE = Tile.getTileSize(); // Size of each tile in pixels
	private static final int GRID_WIDTH = Grid.getGridWidth(); // Number of tiles in the grid
	private static final int GRID_HEIGHT = Grid.getGridHeight(); // Number of tiles in the grid

	private final Item[][] testItems;
	private final ArrayList<Entity> entities;
	private final ArrayList<Entity> overlayEntities;

	private Grid grid;

	private InputHandler inputHandler;
	private ItemMover itemMover;
	private Camera camera; // Initialize camera with speed

	private volatile boolean hasEnded = false;

	public GameState(Item[][] testItems, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, Grid grid,
			InputHandler inputHandler, ItemMover itemMover, Camera camera) {
		this.testItems = testItems;
		this.entities = entities;
		this.overlayEntities = overlayEntities;
		this.grid = grid;
		this.inputHandler = inputHandler;
		this.itemMover = itemMover;
		this.camera = camera;

	}

	public GameState copy() {
		ArrayList<Entity> entitiesCopy = new ArrayList<>(entities);
		ArrayList<Entity> overlayEntitiesCopy = new ArrayList<>(overlayEntities);
		Item[][] testItemsCopy = new Item[GRID_WIDTH][GRID_HEIGHT];
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				testItemsCopy[x][y] = testItems[x][y]; // Copy each item
			}
		}
		Camera cameraCopy = new Camera(this.camera.getCameraX(), this.camera.getCameraY(), Camera.getCameraSpeed(),
				inputHandler, this.camera.getZoom());
		InputHandler inputHandlerCopy = new InputHandler(inputHandler);
		ItemMover itemMoverCopy = new ItemMover(this.itemMover);

		return new GameState(testItemsCopy, entitiesCopy, overlayEntitiesCopy, grid, inputHandlerCopy, itemMoverCopy,
				cameraCopy);
	}

	public static int getTileSize() {
		return TILE_SIZE;
	}

	public static int getGridWidth() {
		return GRID_WIDTH;
	}

	public static int getGridHeight() {
		return GRID_HEIGHT;
	}

	public Grid getGrid() {
		return grid;
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public ItemMover getItemMover() {
		return itemMover;
	}

	public Camera getCamera() {
		return camera;
	}

	public boolean isHasEnded() {
		return hasEnded;
	}

	public Item[][] getTestItems() {
		return testItems;
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public ArrayList<Entity> getOverlayEntities() {
		return overlayEntities;
	}

	public void clearItemAt(int x, int y) {
		if (x >= 0 && y >= 0 && x < testItems.length && y < testItems[0].length) {
			testItems[x][y] = null;
		}
	}

	public Entity getEntityAt(int x, int y) {
		for (Entity e : entities) {
			// Check if the point (x,y) is within the entity's bounds
			if (x >= e.getX() && x < e.getX() + e.getWidth() && y >= e.getY() && y < e.getY() + e.getHeight()) {
				return e;
			}
		}
		for (Entity e : overlayEntities) {
			// Check if the point (x,y) is within the entity's bounds
			if (x >= e.getX() && x < e.getX() + e.getWidth() && y >= e.getY() && y < e.getY() + e.getHeight()) {
				return e;
			}
		}
		return null;
	}

	public Entity getOverlayEntityAt(int x, int y) {
		for (Entity e : overlayEntities) {
			if (x >= e.getX() && x < e.getX() + e.getWidth() && y >= e.getY() && y < e.getY() + e.getHeight()) {
				return e;
			}
		}
		return null;
	}

	public boolean canPlaceEntityAt(int x, int y, Entity newEntity) {
//		System.out.println("Hello from canPlaceEntityAt");
		Tile[][] tileMap = grid.getTiles();
		// Check tile bounds
		if (x < 0 || y < 0 || x + newEntity.getWidth() > GRID_WIDTH || y + newEntity.getHeight() > GRID_HEIGHT) {
			return false;
		}
		// Check delivery zone
		for (Entity e : entities) {
			if (e instanceof DeliveryZone) {
				// Check if any part of the new entity overlaps with the delivery zone
				if (x < e.getX() + e.getWidth() && x + newEntity.getWidth() > e.getX() && y < e.getY() + e.getHeight()
						&& y + newEntity.getHeight() > e.getY()) {
					return false;
				}
			}
		}

		// Only extractor can be placed on resource tile
		for (int dx = 0; dx < newEntity.getWidth(); dx++) {
			for (int dy = 0; dy < newEntity.getHeight(); dy++) {
				int checkX = x + dx;
				int checkY = y + dy;
				if (tileMap[checkX][checkY].isResource() && !(newEntity instanceof Extractor)) {
					return false;
				}
			}
		}

		// Check in entities
		for (Entity e : entities) {
			// Check if any part of the new entity overlaps with existing entities
			if (x < e.getX() + e.getWidth() && x + newEntity.getWidth() > e.getX() && y < e.getY() + e.getHeight()
					&& y + newEntity.getHeight() > e.getY()) {
				boolean tunnelBeltCombo = ((e instanceof Conveyor
						&& (newEntity instanceof Tunnel || newEntity instanceof TunnelEnd))
						|| ((e instanceof Tunnel || e instanceof TunnelEnd) && newEntity instanceof Conveyor));
				if (!tunnelBeltCombo)
					return false;
			}
		}

		// Check in overlayEntities
		for (Entity e : overlayEntities) {
			// Check if any part of the new entity overlaps with existing overlay entities
			if (x < e.getX() + e.getWidth() && x + newEntity.getWidth() > e.getX() && y < e.getY() + e.getHeight()
					&& y + newEntity.getHeight() > e.getY()) {
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
