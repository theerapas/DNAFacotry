package main;

public class Config {
	// Grid/tile logic
	public static final int TILE_SIZE = 64;
    public static final int GRID_WIDTH = 20;
    public static final int GRID_HEIGHT = 10;

    // Total world size in pixels (for camera bounds, etc.)
    public static final int WORLD_WIDTH = TILE_SIZE * GRID_WIDTH;
    public static final int WORLD_HEIGHT = TILE_SIZE * GRID_HEIGHT;

    // Fixed screen size (e.g. window size)
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
}
