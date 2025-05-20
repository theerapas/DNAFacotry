package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import utils.AssetManager;
import utils.Direction;
import utils.Game;
import utils.ItemMover;
import java.util.HashMap;
import java.util.Map;
import utils.SoundManager;

public class Ribosome extends Entity {
	private static final int WIDTH = 1;
	private static final int HEIGHT = 1;
	private static final int MAX_SEQUENCE_LENGTH = 9;
	private Mode currentMode = Mode.RIBOSOME1;
	private StringBuilder collectedSequence = new StringBuilder();
	private Item outputBuffer = null;
	private boolean hasInvalidSequence = false;
	private int invalidSequenceTimer = 0;
	private static final int INVALID_SEQUENCE_DURATION = 30;

	public enum Mode {
		RIBOSOME1("ATGGGATAA", "ENZYME", "TCTCCGG", "ANTIBODY"),
		RIBOSOME2("ATGGGATAA", "MUSCLE", "TCTCCGG", "BLOOD", "CCGAGA", "BRAIN");

		private final Map<String, String> sequenceToTrait;

		Mode(String... mappings) {
			this.sequenceToTrait = new HashMap<>();
			for (int i = 0; i < mappings.length; i += 2) {
				sequenceToTrait.put(mappings[i], mappings[i + 1]);
			}
		}

		public String getTraitForSequence(String sequence) {
			return sequenceToTrait.get(sequence);
		}

		public boolean isValidSequence(String sequence) {
			return sequenceToTrait.containsKey(sequence);
		}

		public int getMinSequenceLength() {
			return sequenceToTrait.keySet().stream().mapToInt(String::length).min().orElse(1);
		}

		public int getMaxSequenceLength() {
			return sequenceToTrait.keySet().stream().mapToInt(String::length).max().orElse(9);
		}

		public boolean isPrefixOfAnyValidSequence(String prefix) {
			for (String valid : sequenceToTrait.keySet()) {
				if (valid.startsWith(prefix))
					return true;
			}
			return false;
		}

	}

	public Ribosome(int x, int y, Direction direction) {
		super(x, y, direction);
		this.width = WIDTH;
		this.height = HEIGHT;
	}

	public void setMode(Mode mode) {
		this.currentMode = mode;
		this.collectedSequence = new StringBuilder();
		this.outputBuffer = null;
		this.hasInvalidSequence = false;
		this.invalidSequenceTimer = 0;

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
		case RIBOSOME1 -> gc.drawImage(AssetManager.ribosome1, -width * tileSize / 2.0 - 16,
				-height * tileSize / 2.0 - 17, width * tileSize * 2, height * tileSize * 2); // Magic number to make it
																								// bigger than its tile
		case RIBOSOME2 -> gc.drawImage(AssetManager.ribosome2, -width * tileSize / 2.0 - 16,
				-height * tileSize / 2.0 - 17, width * tileSize * 2, height * tileSize * 2);
		}

		if (hasInvalidSequence) {
			// Draw a red tint over the ribosome when it has an invalid sequence
			gc.setGlobalAlpha(0.3);
			gc.setFill(javafx.scene.paint.Color.RED);
			gc.fillRect(-width * tileSize / 2.0, -height * tileSize / 2.0, width * tileSize, height * tileSize);
			gc.setGlobalAlpha(1.0);
		}

		gc.restore();
	}

	private boolean inBounds(Item[][] grid, int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}

	private int[] getInputPosition() {
		Direction inputDir = direction.opposite();
		return new int[] { x + inputDir.dx, y + inputDir.dy };
	}

	private int[] getOutputPosition() {
		return new int[] { x + direction.dx, y + direction.dy };
	}

	private boolean canAcceptNucleotide(Item item) {
		return item != null && item.getType() == Item.ItemType.NUCLEOTIDE && item.getDnaCode().length() == 1;
	}

	private int getMinValidSequenceLength() {
		return currentMode.getMinSequenceLength();
	}

	private int getMaxValidSequenceLength() {
		return currentMode.getMaxSequenceLength();
	}

	private void processCollectedSequence() {
		String sequence = collectedSequence.toString();
		boolean matched = false;

		// Try every starting point
		for (int i = 0; i < sequence.length(); i++) {
			// For each possible end (starting from i+1 to end of buffer)
			for (int j = i + 1; j <= sequence.length(); j++) {
				String sub = sequence.substring(i, j);
				if (currentMode.isValidSequence(sub)) {
					String trait = currentMode.getTraitForSequence(sub);
					outputBuffer = new Item(Item.ItemType.TRAIT, trait);

					// Trim buffer after matched region
					collectedSequence = new StringBuilder(sequence.substring(j));
					hasInvalidSequence = false;
					return;
				}
			}
		}

		int safeLength = getMaxValidSequenceLength();

		if (sequence.length() > safeLength) {
			// Check if any suffix could still be a prefix of a valid sequence
			boolean hasPossibleSuffix = false;
			for (int i = sequence.length() - safeLength; i < sequence.length(); i++) {
				String suffix = sequence.substring(i);
				if (currentMode.isPrefixOfAnyValidSequence(suffix)) {
					hasPossibleSuffix = true;
					break;
				}
			}
			if (!hasPossibleSuffix) {
				// No valid suffix found → trim to safe max
				collectedSequence = new StringBuilder(sequence.substring(sequence.length() - safeLength));
			}
		}

		// Only fart if not already showing red
		if (!hasInvalidSequence && sequence.length() >= getMinValidSequenceLength()
				&& !currentMode.isPrefixOfAnyValidSequence(sequence)) {
			SoundManager.play(SoundManager.SOUND_FART);
			hasInvalidSequence = true;
			invalidSequenceTimer = INVALID_SEQUENCE_DURATION;
		}

	}

	@Override
	public void update(Item[][] itemGrid, ItemMover mover) {
		// Update invalid sequence timer
		if (hasInvalidSequence) {
			invalidSequenceTimer--;
			if (invalidSequenceTimer <= 0) {
				hasInvalidSequence = false;
			}
		}

		// Handle output if we have a buffered item
		if (outputBuffer != null) {
			int[] outputPos = getOutputPosition();
			if (inBounds(itemGrid, outputPos[0], outputPos[1]) && itemGrid[outputPos[0]][outputPos[1]] == null) {
				Entity outputEntity = Game.instance.getEntityAt(outputPos[0], outputPos[1]);
				if (outputEntity != null && outputEntity.canAcceptItemFrom(direction)) {
					itemGrid[outputPos[0]][outputPos[1]] = outputBuffer;
					outputBuffer = null;
				}
			}
			return;
		}

		// Process input
		int[] inputPos = getInputPosition();
		if (!inBounds(itemGrid, inputPos[0], inputPos[1]))
			return;

		Item inputItem = itemGrid[inputPos[0]][inputPos[1]];
		if (inputItem != null && canAcceptNucleotide(inputItem)) {
			Entity inputEntity = Game.instance.getEntityAt(inputPos[0], inputPos[1]);
			if (inputEntity != null && inputEntity.getDirection() == direction) {
				collectedSequence.append(inputItem.getDnaCode());
				itemGrid[inputPos[0]][inputPos[1]] = null;
				processCollectedSequence();
			}
		}
	}

}