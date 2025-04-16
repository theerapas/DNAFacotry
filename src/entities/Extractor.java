package entities;

import items.Item;
import items.NucleotideFactory;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import utils.Direction;
import utils.Game;

public class Extractor extends Entity {
	
	private Image image;

	private int tickCounter = 0;
	private int spawnInterval = 60; // every 60 ticks
	private String nucleotideType; // "A", "T", "G", or "C"

	public Extractor(int x, int y, Direction direction, String nucleotideType) {
		super(x, y, direction);
		this.nucleotideType = nucleotideType;
		
		String fileName = "extractor_" + nucleotideType.toLowerCase() + ".png";
		
		image = new Image(ClassLoader.getSystemResourceAsStream("assets/" + fileName));
	}

	@Override
	public void update(Item[][] itemGrid) {
		tickCounter++;
		if (tickCounter < spawnInterval) return;
		tickCounter = 0;

		int tx = x + direction.dx;
		int ty = y + direction.dy;

		// Check bounds + if target tile is empty
		if (!inBounds(itemGrid, tx, ty) || itemGrid[tx][ty] != null)
			return;

		// Check if there is an entity AND it can accept
		Entity target = Game.instance.getEntityAt(tx, ty);
		if (target == null || !target.canAcceptItemFrom(direction))
			return;

		// Finally spawn the item
		itemGrid[tx][ty] = NucleotideFactory.getNucleotide(nucleotideType);
	}


	@Override
	public void render(GraphicsContext gc, int tileSize) {
		gc.drawImage(image, x * tileSize, y * tileSize, tileSize, tileSize);
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}
}
