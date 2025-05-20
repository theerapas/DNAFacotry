package grid;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.AssetManager;
import utils.Camera;

public class Tile {
	private int x, y;
	private static final int TILE_SIZE = 32;

	public enum Type {
		EMPTY, RESOURCE
	}

	private Type type = Type.EMPTY;
	private String resource = null; // "A", "T", "G", "C" if it's a resource tile

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setResource(String code) {
		this.type = Type.RESOURCE;
		this.resource = code;
	}

	public boolean isResource() {
		return type == Type.RESOURCE;
	}

	public String getResource() {
		return resource;
	}

	public Type getType() {
		return type;
	}

	public static int getTileSize() {
		return TILE_SIZE;
	}

	public void drawTile(GraphicsContext gc) {
		if (this.isResource()) {
			switch (getResource()) {
			case "A" -> gc.drawImage(AssetManager.resourceA, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			case "T" -> gc.drawImage(AssetManager.resourceT, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			case "G" -> gc.drawImage(AssetManager.resourceG, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			case "C" -> gc.drawImage(AssetManager.resourceC, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}

		}

	}
}
