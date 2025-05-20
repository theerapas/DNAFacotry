package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import utils.AssetManager;
import utils.Direction;
import utils.Game;
import utils.ItemMover;

public class Conveyor extends Entity {

	private static final double PROGRESS_PER_SECOND = 1;
	private static final double PROGRESS_PER_TICK = PROGRESS_PER_SECOND / 240;

	public static enum ConveyorType {
		STRAIGHT, CURVE_LEFT_TO_TOP, CURVE_LEFT_TO_BOTTOM, CURVE_TOP_TO_LEFT, CURVE_TOP_TO_RIGHT, CURVE_RIGHT_TO_TOP,
		CURVE_RIGHT_TO_BOTTOM, CURVE_BOTTOM_TO_LEFT, CURVE_BOTTOM_TO_RIGHT
	}

	private ConveyorType type;

	public Conveyor(int x, int y, Direction direction, ConveyorType type) {
		super(x, y, direction);
		this.type = type;
	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {

		int targetX;
		int targetY;

		if (this.type == ConveyorType.STRAIGHT) {
			targetX = x + direction.dx;
			targetY = y + direction.dy;
		} else if (this.type == ConveyorType.CURVE_LEFT_TO_TOP || this.type == ConveyorType.CURVE_RIGHT_TO_TOP) {
			targetX = x;
			targetY = y - 1;
		} else if (this.type == ConveyorType.CURVE_LEFT_TO_BOTTOM || this.type == ConveyorType.CURVE_RIGHT_TO_BOTTOM) {
			targetX = x;
			targetY = y + 1;
		} else if (this.type == ConveyorType.CURVE_TOP_TO_LEFT || this.type == ConveyorType.CURVE_BOTTOM_TO_LEFT) {
			targetX = x - 1;
			targetY = y;
		} else {
			targetX = x + 1;
			targetY = y;
		}

		if (!inBounds(itemGrid, targetX, targetY))
			return;
		if (itemGrid[x][y] == null)
			return;

		Item item = itemGrid[x][y];
		increaseProgress(item);

		if (itemGrid[targetX][targetY] != null)
			return;
//	    System.out.println("Progress: " + item.getProgress());

		if (item.getProgress() < 1)
			return;

		Entity overlayEntity = Game.instance.getOverlayEntityAt(x, y);
		if (overlayEntity instanceof Tunnel) {
			return;
		}

		Entity target = Game.instance.getEntityAt(targetX, targetY);
		if (target == null || !target.canAcceptItemFrom(direction))
			return;

		mover.tryMove(x, y, targetX, targetY);
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
		gc.translate(centerX, centerY); // Move pivot to center of tile
		if (this.type == ConveyorType.STRAIGHT) {
			gc.rotate(angle); // Rotate around the center
			gc.drawImage(AssetManager.conveyor, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_BOTTOM_TO_RIGHT) {
			gc.drawImage(AssetManager.conveyorCurveBottomToRight, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_LEFT_TO_BOTTOM) {
			gc.rotate(90);
			gc.drawImage(AssetManager.conveyorCurveBottomToRight, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_TOP_TO_LEFT) {
			gc.rotate(180);
			gc.drawImage(AssetManager.conveyorCurveBottomToRight, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_RIGHT_TO_TOP) {
			gc.rotate(270);
			gc.drawImage(AssetManager.conveyorCurveBottomToRight, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_BOTTOM_TO_LEFT) {
			gc.drawImage(AssetManager.conveyorCurveBottomToLeft, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_LEFT_TO_TOP) {
			gc.rotate(90);
			gc.drawImage(AssetManager.conveyorCurveBottomToLeft, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_TOP_TO_RIGHT) {
			gc.rotate(180);
			gc.drawImage(AssetManager.conveyorCurveBottomToLeft, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		} else if (this.type == ConveyorType.CURVE_RIGHT_TO_BOTTOM) {
			gc.rotate(270);
			gc.drawImage(AssetManager.conveyorCurveBottomToLeft, -tileSize / 2.0, -tileSize / 2.0, tileSize, tileSize);
		}
		gc.restore();
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}

	@Override
	public boolean canAcceptItemFrom(Direction fromDirection) {
		return fromDirection != direction.opposite();
	}

	private void increaseProgress(Item item) {
		item.setProgress(item.getProgress() + PROGRESS_PER_TICK);
	}

	public ConveyorType getType() {
		return type;
	}

	public void setType(ConveyorType type) {
		this.type = type;
	}
}
