package entities;

import items.Item;
import utils.Direction;
import utils.ItemMover;

public abstract class Entity {
	protected int x, y;
	protected Direction direction;
	protected int width = 1; // Default size is 1x1
	protected int height = 1;

	public Entity(int x, int y, Direction direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public abstract void update(Item[][] itemGrid, ItemMover mover);

	public abstract void render(javafx.scene.canvas.GraphicsContext gc, int tileSize);

	public void renderWithTransparency(javafx.scene.canvas.GraphicsContext gc, int tileSize, double alpha) {
		double originalAlpha = gc.getGlobalAlpha();
		gc.setGlobalAlpha(alpha);
		render(gc, tileSize);
		gc.setGlobalAlpha(originalAlpha);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean canAcceptItemFrom(Direction fromDirection) {
		return false;
	}

	public Direction getDirection() {
		return direction;
	}
}