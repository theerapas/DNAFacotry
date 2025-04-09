package utils;

public enum Direction {
	UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

	public final int dx;
	public final int dy;

	Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public Direction rotateClockwise() {
		return values()[(this.ordinal() + 1) % values().length];
	}

	public Direction rotateCounterClockwise() {
		return values()[(this.ordinal() + 3) % values().length];
	}
}