package grid;

import java.util.ArrayList;

import buildMode.BuildMode;
import buildMode.DeleteBuildMode;
import entities.Entity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.InputHandler;
import utils.Camera;
import utils.Direction;
import utils.Game;

// this class has some inconsistencies with the rest of the code
public class Grid {
	private Tile[][] tiles;
	private int cols, rows;
	private static final int GRID_WIDTH = 100;
	private static final int GRID_HEIGHT = 100;

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

	public static int getGridWidth() {
		return GRID_WIDTH;
	}

	public static int getGridHeight() {
		return GRID_HEIGHT;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public void drawTiles(GraphicsContext gc, Camera camera, Grid grid) {
		// === Draw resource tiles (background + letter) ===

		int gridWidth = Grid.getGridWidth();
		int gridHeight = Grid.getGridHeight();
		double cameraX = camera.getCameraX();
		double cameraY = camera.getCameraY();
		int TILE_SIZE = Tile.getTileSize();
		// Make this more efficient by only drawing tiles that are in the camera view
		// (ensure they are within the screen bounds)
		int startX = (int) Math.floor(cameraX / TILE_SIZE);
		startX = startX < 0 ? 0 : startX;
		startX = startX >= gridWidth ? gridWidth - 1 : startX;
		int startY = (int) Math.floor(cameraY / TILE_SIZE);
		startY = startY < 0 ? 0 : startY;
		startY = startY >= gridHeight ? gridHeight - 1 : startY;
		int endX = (int) Math.ceil((cameraX + camera.getScreenWidth()) / TILE_SIZE);
		endX = endX < 0 ? 0 : endX;
		endX = endX >= gridWidth ? gridWidth - 1 : endX;
		int endY = (int) Math.ceil((cameraY + camera.getScreenHeight()) / TILE_SIZE);
		endY = endY < 0 ? 0 : endY;
		endY = endY >= gridHeight ? gridHeight - 1 : endY;

		Tile[][] tileMap = grid.getTiles();

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				tileMap[x][y].drawTile(gc);
			}
		}

	}

	public void drawGrid(GraphicsContext gc, Camera camera) {
		drawTiles(gc, camera, this);
		int TILE_SIZE = Tile.getTileSize();
		// Draw grid
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				gc.setStroke(Color.LIGHTGRAY);
				gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			}
		}
	}

	public void drawGhostPreview(GraphicsContext gc, Camera camera, InputHandler inputHandler) {
		int TILE_SIZE = Tile.getTileSize();
		double cameraX = camera.getCameraX();
		double cameraY = camera.getCameraY();
		double mouseX = inputHandler.getMouseX();
		double mouseY = inputHandler.getMouseY();
		double mouseRelX = mouseX / camera.getZoom() + cameraX;
		double mouseRelY = mouseY / camera.getZoom() + cameraY;
		int tileX = (int) (mouseRelX / TILE_SIZE);
		int tileY = (int) (mouseRelY / TILE_SIZE);

		if (mouseRelX >= 0 && mouseRelY >= 0 && mouseRelX < GRID_WIDTH * TILE_SIZE
				&& mouseRelY < GRID_HEIGHT * TILE_SIZE) {

			// Preview icon based on build mode and direction
			BuildMode buildMode = inputHandler.getBuildMode();
			Direction direction = inputHandler.getDirection();
			Entity previewEntity = buildMode.preview(tileX, tileY, direction);

			if (buildMode instanceof DeleteBuildMode) {
				// Draw red cross directly
				gc.setStroke(Color.RED);
				gc.setLineWidth(2);

				// Draw diagonal lines
				double padding = TILE_SIZE * 0.2; // 20% padding from edges
				gc.strokeLine(tileX * TILE_SIZE + padding, tileY * TILE_SIZE + padding,
						(tileX + 1) * TILE_SIZE - padding, (tileY + 1) * TILE_SIZE - padding);
				gc.strokeLine(tileX * TILE_SIZE + padding, (tileY + 1) * TILE_SIZE - padding,
						(tileX + 1) * TILE_SIZE - padding, tileY * TILE_SIZE + padding);
				return;
			}

			if (previewEntity == null || !Game.instance.canPlaceEntityAt(tileX, tileY, previewEntity)) {
				gc.setFill(Color.RED);
				gc.setGlobalAlpha(0.5);
				gc.fillRect(tileX * TILE_SIZE, tileY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				gc.setGlobalAlpha(1.0);
			} else {
				previewEntity.renderWithTransparency(gc, TILE_SIZE, 0.5);
			}
		}
	}
}