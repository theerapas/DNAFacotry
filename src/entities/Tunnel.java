package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utils.AssetManager;
import utils.Direction;
import utils.Game;
import utils.ItemMover;

public class Tunnel extends Entity {

	private int exitX, exitY;

	public Tunnel(int x, int y, Direction direction, int exitX, int exitY) {
		super(x, y, direction); // direction should be UP or DOWN
		this.exitX = exitX;
		this.exitY = exitY;
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
		gc.drawImage(AssetManager.tunnel, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		gc.restore();
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}

	public void setExit(int x, int y) {
		this.exitX = x;
		this.exitY = y;
	}

	public void clearExitIfMatches(int tx, int ty) {
		if (this.exitX == tx && this.exitY == ty) {
			this.exitX = -1;
			this.exitY = -1;
//			System.out.println("Tunnel exit cleared for entrance at (" + x + "," + y + ")");
		}
	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {
		if (exitX == -1 || exitY == -1)
			return;

		Item incoming = itemGrid[x][y];

		// 1. Only act if there's an item to teleport
		if (incoming == null)
			return;

		// 2. Make sure the tunnel is aligned
		boolean aligned = (x == exitX || y == exitY);
		if (!aligned)
			return;

		// 3. Directional logic: tunnel must point toward the exit
		switch (direction) {
		case UP -> {
			if (exitY >= y)
				return;
		}
		case DOWN -> {
			if (exitY <= y)
				return;
		}
		case LEFT -> {
			if (exitX >= x)
				return;
		}
		case RIGHT -> {
			if (exitX <= x)
				return;
		}
		}

		// 3. Only move if destination is empty
		if (itemGrid[exitX][exitY] == null) {
			itemGrid[x][y] = null;
			itemGrid[exitX][exitY] = incoming;
		}
	}
}
