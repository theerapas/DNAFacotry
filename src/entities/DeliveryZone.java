package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import logic.GoalManager;
import utils.Direction;

public class DeliveryZone extends Entity {
	
	private Image image;
	
	public DeliveryZone(int x, int y) {
		super(x, y, Direction.UP); // direction unused
		image = new Image(ClassLoader.getSystemResourceAsStream("assets/delivery.png"));
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
		gc.drawImage(image, x * tileSize, y * tileSize, tileSize, tileSize);
	}
	
	@Override
	public boolean canAcceptItemFrom(Direction fromDirection) {
	    return true; // Accept lifeforms from any direction
	}

}
