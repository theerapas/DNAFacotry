package entities;

import items.Item;
import items.TraitCombiner;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Direction;

public class DNACombiner extends Entity {

    private Item buffer = null; // First item waiting

    public DNACombiner(int x, int y, Direction direction) {
        super(x, y, direction);
    }

    @Override
    public void update(Item[][] itemGrid) {
        int inputX = x - direction.dx;
        int inputY = y - direction.dy;
        int outputX = x + direction.dx;
        int outputY = y + direction.dy;

        if (!inBounds(itemGrid, inputX, inputY) || !inBounds(itemGrid, outputX, outputY)) return;

        Item incoming = itemGrid[inputX][inputY];

        if (incoming != null) {
            if (buffer == null) {
                // Store first input
                buffer = incoming;
                itemGrid[inputX][inputY] = null;
            } else {
                // Try combine with buffer
                Item combined = TraitCombiner.tryCombine(buffer, incoming);
                if (combined != null && itemGrid[outputX][outputY] == null) {
                    itemGrid[outputX][outputY] = combined;
                    itemGrid[inputX][inputY] = null;
                    buffer = null;
                }
            }
        }
    }

    @Override
    public void render(GraphicsContext gc, int tileSize) {
        gc.setFill(Color.DARKVIOLET);
        gc.fillRoundRect(x * tileSize + 4, y * tileSize + 4, tileSize - 8, tileSize - 8, 8, 8);
        gc.setStroke(Color.WHITE);
        gc.strokeText("⚗", x * tileSize + tileSize / 2 - 5, y * tileSize + tileSize / 2 + 5);
    }

    private boolean inBounds(Item[][] grid, int x, int y) {
        return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
    }
}
