package entities;

import items.Item;
import items.NucleotideFactory;
import javafx.scene.canvas.GraphicsContext;
import utils.AssetManager;
import utils.Direction;
import utils.Game;
import utils.ItemMover;

public class Extractor extends Entity {

	private int tickCounter = 0;
	private int spawnInterval = 60; // every 60 ticks
	private String nucleotideType; // "A", "T", "G", or "C"

	public Extractor(int x, int y, Direction direction, String nucleotideType) {
		super(x, y, direction);
		this.nucleotideType = nucleotideType;
	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {
		tickCounter++;
		if (tickCounter < spawnInterval)
			return;
		tickCounter = 0;
		int tx = x + direction.dx;
		int ty = y + direction.dy;

		// Check bounds
		if (!inBounds(itemGrid, tx, ty)) {
			return;
		}
		// Don’t spawn if tile is already occupied
		if (itemGrid[tx][ty] != null) {
			return;
		}
		// Check if the entity at (tx, ty) is valid receiver
		Entity target = Game.instance.getEntityAt(tx, ty);
		if (target == null || !target.getDirection().equals(direction)) {
			return;
		}
		// Spawn a new nucleotide via mover system
		mover.trySpawn(x, y, tx, ty, NucleotideFactory.getNucleotide(nucleotideType));
	}

	@Override
	public void render(GraphicsContext gc, int tileSize) {
		double centerX = x * tileSize + tileSize / 2.0;
		double centerY = y * tileSize + tileSize / 2.0;
		double angle = switch (direction) {
		case RIGHT -> 0;
		case DOWN -> 90;
		case LEFT -> 180;
		case UP -> 270;
		};

		gc.save();
		gc.translate(centerX, centerY); // Move pivot to center of tile
		gc.rotate(angle); // Rotate around the center
		switch (nucleotideType) {
		case "A" -> gc.drawImage(AssetManager.extractorA, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		case "T" -> gc.drawImage(AssetManager.extractorT, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		case "G" -> gc.drawImage(AssetManager.extractorG, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		case "C" -> gc.drawImage(AssetManager.extractorC, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		}

		gc.restore();
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}
}
