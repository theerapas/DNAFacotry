package entities;

import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utils.Direction;
import utils.Game;

public class Conveyor extends Entity {
    private Image image;
    private float progress = 0f;
    private static final float MOVE_TIME = 0.8f; // seconds
    private long lastTickTime = System.nanoTime();

    public Conveyor(int x, int y, Direction direction) {
        super(x, y, direction);

        String fileName = switch (direction) {
            case RIGHT -> "conveyor_right.png";
            case LEFT  -> "conveyor_left.png";
            case UP    -> "conveyor_up.png";
            case DOWN  -> "conveyor_down.png";
        };

        image = new Image(ClassLoader.getSystemResourceAsStream("assets/" + fileName));
    }

    @Override
    public void update(Item[][] itemGrid) {
    	int targetX = x + direction.dx;
    	int targetY = y + direction.dy;

    	// Check bounds
    	if (!inBounds(itemGrid, targetX, targetY)) return;

    	// Only move if there's an item here, and the destination is empty
    	if (itemGrid[x][y] == null || itemGrid[targetX][targetY] != null) return;

    	// Get target entity
    	Entity target = Game.instance.getEntityAt(targetX, targetY);

    	// If there's no entity at destination → don't move
    	if (target == null) return;

    	// If there's an entity but it doesn't accept from this direction → don't move
    	if (!target.canAcceptItemFrom(direction)) return;

    	// Everything OK — move the item
    	itemGrid[targetX][targetY] = itemGrid[x][y];
    	itemGrid[x][y] = null;
    }

    
    private boolean inBounds(Item[][] grid, int x, int y) {
    	return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length;
    }


    public void tickProgress() {
        long now = System.nanoTime();
        float delta = (now - lastTickTime) / 1_000_000_000f;
        lastTickTime = now;
        progress += delta;
    }

    public void resetProgress() {
        progress = 0f;
        lastTickTime = System.nanoTime();
    }

    public float getProgress() {
        return Math.min(progress / MOVE_TIME, 1f);
    }

    public boolean isReadyToMove() {
        return progress >= MOVE_TIME;
    }

    @Override
    public boolean canAcceptItemFrom(Direction fromDirection) {
        return fromDirection != direction.opposite();
    }

    @Override
    public void render(GraphicsContext gc, int tileSize) {
        gc.drawImage(image, x * tileSize, y * tileSize, tileSize, tileSize);
    }
}
