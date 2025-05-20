package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utils.AssetManager;
import utils.Direction;
import utils.ItemMover;

public class TunnelEnd extends Entity {

	public TunnelEnd(int x, int y, Direction direction) {
		super(x, y, direction);
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
	    gc.translate(centerX, centerY);         // Move pivot to center of tile
	    gc.rotate(angle);                        // Rotate around the center
	    gc.drawImage(AssetManager.tunnel_exit, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
	    gc.restore();
	}

	@Override
	public boolean canAcceptItemFrom(Direction fromDirection) {
		// Same logic as conveyor: accept input from any direction except opposite
		return fromDirection != direction.opposite();
	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {
	}
}

