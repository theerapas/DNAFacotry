package entities;

import items.Item;
import items.NucleotideFactory;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Direction;

public class Extractor extends Entity {
	private int tickCounter = 0;
    private int spawnInterval = 60; // every 60 ticks (~1 second)

    public Extractor(int x, int y, Direction direction) {
        super(x, y, direction);
    }

    @Override
    public void update(Item[][] itemGrid) {
        tickCounter++;
        if (tickCounter >= spawnInterval) {
            tickCounter = 0;

            int tx = x + direction.dx;
            int ty = y + direction.dy;

            if (inBounds(itemGrid, tx, ty) && itemGrid[tx][ty] == null) {
                itemGrid[tx][ty] = NucleotideFactory.randomNucleotide();
            }
        }
    }

    @Override
    public void render(GraphicsContext gc, int tileSize) {
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(x * tileSize + 4, y * tileSize + 4, tileSize - 8, tileSize - 8);

        gc.setStroke(Color.WHITE);
        gc.strokeText("⛏", x * tileSize + tileSize / 2 - 5, y * tileSize + tileSize / 2 + 5);
    }

    private boolean inBounds(Item[][] grid, int x, int y) {
        return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
    }
}