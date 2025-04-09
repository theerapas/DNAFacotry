package grid;

public class Grid {
	private Tile[][] tiles;
	private int cols, rows;

	public Grid(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		tiles = new Tile[cols][rows];

		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				tiles[x][y] = new Tile(x, y);
			}
		}
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= cols || y >= rows)
			return null;
		return tiles[x][y];
	}
}