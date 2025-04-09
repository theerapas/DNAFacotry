package entities;

import items.Item;
import utils.Direction;

public abstract class Entity {
	protected int x, y;
	protected Direction direction;

	public Entity(int x, int y, Direction direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public abstract void update(Item[][] itemGrid);

	public abstract void render(javafx.scene.canvas.GraphicsContext gc, int tileSize);

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}