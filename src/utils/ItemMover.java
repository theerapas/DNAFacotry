package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import items.Item;

public class ItemMover {
	private record Move(int fromX, int fromY, int toX, int toY, Item item) {
	}

	private Set<String> reservedTargets = new HashSet<>();
	private List<Move> moves = new ArrayList<>();
	private Item[][] grid;
	// memory of what pushed to what last frame
	private Map<String, String> lastPushMap = new HashMap<>(); // key = toX,toY → value = fromX,fromY

	public ItemMover() {
	};

	public ItemMover(ItemMover other) {
		this.grid = other.grid;
		this.moves = new ArrayList<>(other.moves);
		this.reservedTargets = new HashSet<>(other.reservedTargets);
		this.lastPushMap = new HashMap<>(other.lastPushMap);
	}

	public void begin(Item[][] grid) {
		this.grid = grid;
		moves.clear();
		reservedTargets.clear();
	}

	public void tryMove(int fromX, int fromY, int toX, int toY) {
		if (!inBounds(toX, toY))
			return;
		if (grid[fromX][fromY] == null)
			return;

		String key = toX + "," + toY;
		String currentSource = fromX + "," + fromY;
		String previousSource = lastPushMap.get(key);
		// ❗ This MUST come BEFORE the check for destination being empty
		if (previousSource != null && !previousSource.equals(currentSource)) {
			// System.out.println("Blocked by lastPushMap! Previous source was " +
			// previousSource);
			return;
		}

		if (grid[toX][toY] != null)
			return;

		if (reservedTargets.contains(key)) {
			// System.out.println("Blocked by reservedTargets! Already reserved in this
			// frame");
			return;
		}

		grid[fromX][fromY].setProgress(0);
		reservedTargets.add(key);
		moves.add(new Move(fromX, fromY, toX, toY, grid[fromX][fromY]));
	}

	public void trySpawn(int fromX, int fromY, int toX, int toY, Item item) {
		if (!inBounds(toX, toY))
			return;

		String key = toX + "," + toY;
		String currentSource = fromX + "," + fromY;
		String previousSource = lastPushMap.get(key);

		if (previousSource != null && !previousSource.equals(currentSource)) {
			return; // Someone else owns this tile
		}

		if (grid[toX][toY] != null || reservedTargets.contains(key))
			return;

		reservedTargets.add(key);
		moves.add(new Move(fromX, fromY, toX, toY, item));
	}

	public void executeMoves() {
		// System.out.println("---- Executing moves ----");

		Map<String, String> newPushMap = new HashMap<>();

		for (Move move : moves) {
			grid[move.toX][move.toY] = move.item;
			if (move.fromX != -1 && move.fromY != -1) {
				grid[move.fromX][move.fromY] = null;
			}
			// System.out.println("Moving from " + move.fromX + "," + move.fromY + " → " +
			// move.toX + "," + move.toY);

			newPushMap.put(move.toX + "," + move.toY, move.fromX + "," + move.fromY);
		}

		// lastPushMap.clear();
		lastPushMap.putAll(newPushMap); // Update memory *after* all moves are committed

		moves.clear();
		reservedTargets.clear();
	}

	public void forgetOwnership(int x, int y) {
		String key = x + "," + y;
		lastPushMap.remove(key);
		// delete from value
		for (String k : lastPushMap.keySet()) {
			if (lastPushMap.get(k).equals(key)) {
				lastPushMap.remove(k);
				break;
			}
		}
	}

	private boolean inBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
	}

	public Set<String> getReservedTargets() {
		return reservedTargets;
	}

	public List<Move> getMoves() {
		return moves;
	}

	public Map<String, String> getLastPushMap() {
		return lastPushMap;
	}
}
