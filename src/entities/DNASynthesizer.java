package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import utils.AssetManager;
import utils.Direction;
import utils.Game;
import utils.ItemMover;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DNASynthesizer extends Entity {
	private static final int WIDTH = 3;
	private static final int HEIGHT = 3;
	private Mode currentMode = Mode.MODE1;

	public enum Mode {
		MODE1("ATGGGATAA"), MODE2("TCTCCGG"), MODE3("CCGAGA");

		private final String sequence;
		private final Map<Character, Integer> requiredNucleotides;

		Mode(String sequence) {
			this.sequence = sequence;
			this.requiredNucleotides = new HashMap<>();
			for (char c : sequence.toCharArray()) {
				requiredNucleotides.put(c, requiredNucleotides.getOrDefault(c, 0) + 1);
			}
		}

		public String getSequence() {
			return sequence;
		}

		public Map<Character, Integer> getRequiredNucleotides() {
			return requiredNucleotides;
		}
	}

	private Map<Character, Integer> collectedNucleotides;
	private ArrayList<Item> outputBuffer;

	public DNASynthesizer(int x, int y, Direction direction) {
		super(x, y, direction);
		this.width = WIDTH;
		this.height = HEIGHT;
		this.collectedNucleotides = new HashMap<>();
		this.outputBuffer = new ArrayList<>();
	}

	public void setMode(Mode mode) {
		this.currentMode = mode;
		// Clear collected nucleotides when changing mode
		this.collectedNucleotides.clear();
		this.outputBuffer.clear();
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

		// Draw the appropriate image based on mode
		switch (currentMode) {
		case MODE1 -> gc.drawImage(AssetManager.dnaSynthesizer1, -width * tileSize / 2.0, -height * tileSize / 2.0,
				width * tileSize, height * tileSize);
		case MODE2 -> gc.drawImage(AssetManager.dnaSynthesizer2, -width * tileSize / 2.0, -height * tileSize / 2.0,
				width * tileSize, height * tileSize);
		case MODE3 -> gc.drawImage(AssetManager.dnaSynthesizer3, -width * tileSize / 2.0, -height * tileSize / 2.0,
				width * tileSize, height * tileSize);
		}

		gc.restore();
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}

	private int[][] getInputPositions() {
		int[][] positions = new int[3][2];
		Direction inputDir = direction.opposite();

		switch (direction) {
		case RIGHT -> {
			positions[0] = new int[] { x + inputDir.dx, y + inputDir.dy }; // Top left
			positions[1] = new int[] { x + inputDir.dx, y + 1 + inputDir.dy }; // Middle left
			positions[2] = new int[] { x + inputDir.dx, y + 2 + inputDir.dy }; // Bottom left
		}
		case LEFT -> {
			positions[0] = new int[] { x + width - 1 + inputDir.dx, y + inputDir.dy }; // Top right
			positions[1] = new int[] { x + width - 1 + inputDir.dx, y + 1 + inputDir.dy }; // Middle right
			positions[2] = new int[] { x + width - 1 + inputDir.dx, y + 2 + inputDir.dy }; // Bottom right
		}
		case UP -> {
			positions[0] = new int[] { x + inputDir.dx, y + height - 1 + inputDir.dy }; // Bottom left
			positions[1] = new int[] { x + 1 + inputDir.dx, y + height - 1 + inputDir.dy }; // Bottom middle
			positions[2] = new int[] { x + 2 + inputDir.dx, y + height - 1 + inputDir.dy }; // Bottom right
		}
		case DOWN -> {
			positions[0] = new int[] { x + inputDir.dx, y + inputDir.dy }; // Top left
			positions[1] = new int[] { x + 1 + inputDir.dx, y + inputDir.dy }; // Top middle
			positions[2] = new int[] { x + 2 + inputDir.dx, y + inputDir.dy }; // Top right
		}
		}
		return positions;
	}

	private int[] getOutputPosition() {
		int outputX = x;
		int outputY = y;

		switch (direction) {
		case RIGHT -> {
			outputX = x + width - 1 + direction.dx; // Right edge
			outputY = y + 1 + direction.dy; // Middle
		}
		case LEFT -> {
			outputX = x + direction.dx; // Left edge
			outputY = y + 1 + direction.dy; // Middle
		}
		case UP -> {
			outputX = x + 1 + direction.dx; // Middle
			outputY = y + direction.dy; // Top edge
		}
		case DOWN -> {
			outputX = x + 1 + direction.dx; // Middle
			outputY = y + height - 1 + direction.dy; // Bottom edge
		}
		}

		return new int[] { outputX, outputY };
	}

	private boolean canAcceptNucleotide(Item item) {
		if (item == null || item.getType() != Item.ItemType.NUCLEOTIDE) {
			return false;
		}
		String dnaCode = item.getDnaCode();
		if (dnaCode.length() != 1) {
			return false;
		}
		char nucleotide = dnaCode.charAt(0);
		int currentCount = collectedNucleotides.getOrDefault(nucleotide, 0);
		int requiredCount = currentMode.getRequiredNucleotides().getOrDefault(nucleotide, 0);
		return currentCount < requiredCount;
	}

	private void processCollectedNucleotides() {
		Map<Character, Integer> required = currentMode.getRequiredNucleotides();
		boolean hasAllRequired = true;

		for (Map.Entry<Character, Integer> entry : required.entrySet()) {
			if (collectedNucleotides.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
				hasAllRequired = false;
				break;
			}
		}

		if (hasAllRequired) {
			// Create output sequence
			for (char c : currentMode.getSequence().toCharArray()) {
				outputBuffer.add(new Item(Item.ItemType.NUCLEOTIDE, String.valueOf(c)));
			}
			// Clear collected nucleotides
			collectedNucleotides.clear();
		}
	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {
		// Handle output if we have buffered items
		if (!outputBuffer.isEmpty()) {
			int[] outputPos = getOutputPosition();
			if (inBounds(itemGrid, outputPos[0], outputPos[1]) && itemGrid[outputPos[0]][outputPos[1]] == null) {
				Entity outputEntity = Game.instance.getEntityAt(outputPos[0], outputPos[1]);
				if (outputEntity != null && outputEntity.canAcceptItemFrom(direction)) {
					itemGrid[outputPos[0]][outputPos[1]] = outputBuffer.remove(0);
				}
			}
			return;
		}

		// Process inputs
		int[][] inputPositions = getInputPositions();
		for (int[] pos : inputPositions) {
			if (!inBounds(itemGrid, pos[0], pos[1]))
				continue;

			Item inputItem = itemGrid[pos[0]][pos[1]];
			if (inputItem != null && canAcceptNucleotide(inputItem)) {
				Entity inputEntity = Game.instance.getEntityAt(pos[0], pos[1]);
				if (inputEntity != null && inputEntity.getDirection() == direction) {
					char nucleotide = inputItem.getDnaCode().charAt(0);
					collectedNucleotides.put(nucleotide, collectedNucleotides.getOrDefault(nucleotide, 0) + 1);
					itemGrid[pos[0]][pos[1]] = null;
					processCollectedNucleotides();
				}
			}
		}
	}
}