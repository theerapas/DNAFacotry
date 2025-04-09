package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.GoalManager;
import utils.Direction;

public class DeliveryZone extends Entity {

	public DeliveryZone(int x, int y) {
		super(x, y, Direction.UP); // direction unused
	}

	@Override
	public void update(Item[][] itemGrid) {
		Item item = itemGrid[x][y];
		if (item != null && item.getType() == Item.ItemType.LIFEFORM) {
			GoalManager.getInstance().submitLifeform(item.getDnaCode());
			itemGrid[x][y] = null;
		}
	}

	@Override
	public void render(GraphicsContext gc, int tileSize) {
		gc.setFill(Color.GOLD);
		gc.fillRoundRect(x * tileSize + 2, y * tileSize + 2, tileSize - 4, tileSize - 4, 10, 10);
		gc.setFill(Color.BLACK);
		gc.fillText("🎯", x * tileSize + tileSize / 2 - 5, y * tileSize + tileSize / 2 + 5);
	}
}
