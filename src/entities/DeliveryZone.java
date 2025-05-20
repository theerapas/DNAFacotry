package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.GoalManager;
import utils.AssetManager;
import utils.Direction;
import utils.ItemMover;

public class DeliveryZone extends Entity {

	public DeliveryZone(int x, int y) {
		super(x, y, Direction.UP); // direction unused
		this.width = 4;
		this.height = 4;
	}

	@Override
	public void render(GraphicsContext gc, int tileSize) {
		gc.drawImage(AssetManager.deliveryZone, x * tileSize - 18, y * tileSize - 32, width * tileSize * 1.3,
				height * tileSize * 1.4);
	}

	@Override
	public boolean canAcceptItemFrom(Direction fromDirection) {
		return true; // Accept lifeforms from any direction
	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {
		// Check all 16 tiles in the 4x4 area
		for (int dx = 0; dx < width; dx++) {
			for (int dy = 0; dy < height; dy++) {
				int checkX = x + dx;
				int checkY = y + dy;

				// Ensure we're within bounds
				if (!inBounds(itemGrid, checkX, checkY)) {
					continue;
				}

				Item item = itemGrid[checkX][checkY];
				if (item != null) {
					GoalManager.getInstance().submitLifeform(item.getDnaCode());
					itemGrid[checkX][checkY] = null;
					mover.forgetOwnership(checkX, checkY);
				}
			}
		}
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}
}
