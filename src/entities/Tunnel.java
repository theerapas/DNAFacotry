package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import utils.Direction;

public class Tunnel extends Entity {

	private int exitX, exitY;
	private Image image;

	public Tunnel(int x, int y, Direction direction, int exitX, int exitY) {
		super(x, y, direction); // direction should be UP or DOWN
		this.exitX = exitX;
		this.exitY = exitY;

		image = new Image(ClassLoader.getSystemResourceAsStream("assets/tunnel_" + direction.name().toLowerCase() + ".png"));
	}

	@Override
	public void update(Item[][] itemGrid) {
		if (exitX == -1 || exitY == -1) return;

		Item incoming = itemGrid[x][y];

		// Only act if there's an item to teleport
		if (incoming == null) return;

		// 1. Make sure the tunnel is aligned (same row or column)
		boolean aligned = (x == exitX || y == exitY);
		if (!aligned) return;

		// 2. Directional logic (optional, but cool)
		// Tunnel direction must point toward the exit
		switch (direction) {
			case UP -> {
				if (exitY >= y) return;
			}
			case DOWN -> {
				if (exitY <= y) return;
			}
			case LEFT -> {
				if (exitX >= x) return;
			}
			case RIGHT -> {
				if (exitX <= x) return;
			}
		}

		// 3. Only move if destination is empty
		if (itemGrid[exitX][exitY] == null) {
			itemGrid[x][y] = null;
			itemGrid[exitX][exitY] = incoming;
		}
	}


	@Override
	public void render(GraphicsContext gc, int tileSize) {
		gc.drawImage(image, x * tileSize, y * tileSize, tileSize, tileSize);
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
			System.out.println("Tunnel exit cleared for entrance at (" + x + "," + y + ")");
		}
	}


}
