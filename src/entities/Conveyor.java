package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Direction;

public class Conveyor extends Entity {
	public Conveyor(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	@Override
	public void update(Item[][] itemGrid) {
		int targetX = x + direction.dx;
		int targetY = y + direction.dy;

		// Move item from this tile to the next if empty
		if (inBounds(itemGrid, targetX, targetY) && itemGrid[x][y] != null && itemGrid[targetX][targetY] == null) {
			itemGrid[targetX][targetY] = itemGrid[x][y];
			itemGrid[x][y] = null;
		}
	}

	@Override
	public void render(GraphicsContext gc, int tileSize) {
	    gc.setFill(Color.GRAY);
	    gc.fillRect(x * tileSize + 8, y * tileSize + 8, tileSize - 16, tileSize - 16);

	    gc.setFill(Color.BLACK);
	    String arrow = switch (direction) {
	        case UP -> "↑";
	        case RIGHT -> "→";
	        case DOWN -> "↓";
	        case LEFT -> "←";
	    };
	    gc.fillText(arrow, x * tileSize + tileSize / 2 - 5, y * tileSize + tileSize / 2 + 5);
	}


	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}

}
