package entities;

import items.Item;
import items.LifeformCombiner;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Direction;

public class LifeformAssembler extends Entity {

	public LifeformAssembler(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	@Override
	public void update(Item[][] itemGrid) {
		int input1X = x;
		int input1Y = y - 1;
		int input2X = x;
		int input2Y = y + 1;
		int outputX = x + direction.dx;
		int outputY = y + direction.dy;

		if (!inBounds(itemGrid, input1X, input1Y) || !inBounds(itemGrid, input2X, input2Y)
				|| !inBounds(itemGrid, outputX, outputY))
			return;

		Item in1 = itemGrid[input1X][input1Y];
		Item in2 = itemGrid[input2X][input2Y];

		if (in1 != null && in2 != null) {
			Item combined = LifeformCombiner.tryCombine(in1, in2);
			if (combined != null && itemGrid[outputX][outputY] == null) {
				itemGrid[outputX][outputY] = combined;
				itemGrid[input1X][input1Y] = null;
				itemGrid[input2X][input2Y] = null;
			}
		}
	}

	@Override
	public void render(GraphicsContext gc, int tileSize) {
		gc.setFill(Color.DARKBLUE);
		gc.fillRoundRect(x * tileSize + 4, y * tileSize + 4, tileSize - 8, tileSize - 8, 8, 8);
		gc.setStroke(Color.WHITE);
		gc.strokeText("🧬", x * tileSize + tileSize / 2 - 5, y * tileSize + tileSize / 2 + 5);
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}
}
