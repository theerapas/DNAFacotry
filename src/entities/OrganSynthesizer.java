package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import utils.AssetManager;
import utils.Direction;
import utils.Game;
import utils.ItemMover;

public class OrganSynthesizer extends Entity {

	public OrganSynthesizer(int x, int y, Direction direction) {
		super(x, y, direction);
		this.width = 2;
		this.height = 2;
	}

	@Override
	public void render(GraphicsContext gc, int tileSize) {
		double centerX = x * tileSize + (width * tileSize) / 2.0;
		double centerY = y * tileSize + (height * tileSize) / 2.0;
		double angle = switch (direction) {
		case RIGHT -> 0;
		case DOWN -> 90;
		case LEFT -> 180;
		case UP -> 270;
		};

		gc.save();
		gc.translate(centerX, centerY);
		gc.rotate(angle);
		gc.drawImage(AssetManager.organSynthesizer, -width * tileSize / 2.0, -height * tileSize / 2.0, width * tileSize,
				height * tileSize);
		gc.restore();
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}

	private Direction[] getInputDirections() {
		Direction inputDir = direction.opposite();
		return new Direction[] { inputDir, inputDir };
	}

	private int[] getInputPositions(int inputIndex) {
		Direction inputDir = direction.opposite();
		int inputX = x;
		int inputY = y;

		switch (direction) {
		case RIGHT -> {
			inputX = x + inputDir.dx;
			inputY = y + (inputIndex == 0 ? 0 : height - 1) + inputDir.dy;
		}
		case LEFT -> {
			inputX = x + width - 1 + inputDir.dx;
			inputY = y + (inputIndex == 0 ? 0 : height - 1) + inputDir.dy;
		}
		case UP -> {
			inputX = x + (inputIndex == 0 ? 0 : width - 1) + inputDir.dx;
			inputY = y + height - 1 + inputDir.dy;
		}
		case DOWN -> {
			inputX = x + (inputIndex == 0 ? 0 : width - 1) + inputDir.dx;
			inputY = y + inputDir.dy;
		}
		}

		return new int[] { inputX, inputY };
	}

	private int[] getOutputPosition() {
		int outputX = x;
		int outputY = y;

		switch (direction) {
		case RIGHT -> {
			outputX = x + width - 1 + direction.dx;
			outputY = y + height - 1 + direction.dy;
		}
		case LEFT -> {
			outputX = x + direction.dx;
			outputY = y + direction.dy;
		}
		case UP -> {
			outputX = x + width - 1 + direction.dx;
			outputY = y + direction.dy;
		}
		case DOWN -> {
			outputX = x + direction.dx;
			outputY = y + height - 1 + direction.dy;
		}
		}

		return new int[] { outputX, outputY };
	}

	private Item synthesizeOrgan(Item input1, Item input2) {
		if (input1 == null || input2 == null)
			return null;

		if (!isValidInputType(input1) || !isValidInputType(input2))
			return null;

		String code1 = input1.getDnaCode();
		String code2 = input2.getDnaCode();

		// ENZYME + BRAIN → ORGAN_BRAIN
		if ((code1.equals("ENZYME") && code2.equals("BRAIN")) || (code2.equals("ENZYME") && code1.equals("BRAIN"))) {
			return new Item(Item.ItemType.ORGAN, "ORGAN_BRAIN");
		}
		// ANTIBODY + BLOOD → ORGAN_HEART
		if ((code1.equals("ANTIBODY") && code2.equals("BLOOD"))
				|| (code2.equals("ANTIBODY") && code1.equals("BLOOD"))) {
			return new Item(Item.ItemType.ORGAN, "ORGAN_HEART");
		}
		// ANTIBODY + MUSCLE → ORGAN_LUNGS
		if ((code1.equals("ANTIBODY") && code2.equals("MUSCLE"))
				|| (code2.equals("ANTIBODY") && code1.equals("MUSCLE"))) {
			return new Item(Item.ItemType.ORGAN, "ORGAN_LUNGS");
		}

		return null;
	}

	private boolean isValidInputType(Item item) {
		return item.getType() == Item.ItemType.PROTEIN || item.getType() == Item.ItemType.TRAIT;
	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {
		Direction[] inputs = getInputDirections();
		int[] input1Pos = getInputPositions(0);
		int[] input2Pos = getInputPositions(1);
		int[] outputPos = getOutputPosition();

		int input1X = input1Pos[0];
		int input1Y = input1Pos[1];
		int input2X = input2Pos[0];
		int input2Y = input2Pos[1];
		int outputX = outputPos[0];
		int outputY = outputPos[1];

		if (!inBounds(itemGrid, input1X, input1Y) || !inBounds(itemGrid, input2X, input2Y)
				|| !inBounds(itemGrid, outputX, outputY))
			return;

		Item in1 = itemGrid[input1X][input1Y];
		Item in2 = itemGrid[input2X][input2Y];

		if (in1 != null && in2 != null) {
			Entity input1Entity = Game.instance.getEntityAt(input1X, input1Y);
			Entity input2Entity = Game.instance.getEntityAt(input2X, input2Y);

			boolean input1Correct = input1Entity != null && input1Entity.getDirection() == inputs[0].opposite();
			boolean input2Correct = input2Entity != null && input2Entity.getDirection() == inputs[1].opposite();

			if (!input1Correct || !input2Correct)
				return;

			if (itemGrid[outputX][outputY] != null)
				return;

			Entity outputEntity = Game.instance.getEntityAt(outputX, outputY);
			if (outputEntity == null || !outputEntity.canAcceptItemFrom(direction))
				return;

			Item synthesized = synthesizeOrgan(in1, in2);
			if (synthesized != null) {
				itemGrid[outputX][outputY] = synthesized;
				itemGrid[input1X][input1Y] = null;
				itemGrid[input2X][input2Y] = null;
			}
		}
	}
}