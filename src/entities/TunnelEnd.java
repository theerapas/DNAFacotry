package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utils.Direction;

public class TunnelEnd extends Entity {
	private Image image;

	public TunnelEnd(int x, int y, Direction direction) {
		super(x, y, direction);
		String filename = "assets/tunnel_exit_" + direction.name().toLowerCase() + ".png";
		image = new Image(ClassLoader.getSystemResourceAsStream(filename));
	}

	@Override
	public void update(Item[][] itemGrid) {
		// TunnelEnd does nothing except serve as a receiver
	}

	@Override
	public void render(GraphicsContext gc, int tileSize) {
		gc.drawImage(image, x * tileSize, y * tileSize, tileSize, tileSize);
	}

	@Override
	public boolean canAcceptItemFrom(Direction fromDirection) {
		// Same logic as conveyor: accept input from any direction except opposite
		return fromDirection != direction.opposite();
	}
}

