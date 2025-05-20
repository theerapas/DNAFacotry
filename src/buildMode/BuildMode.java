package buildMode;

import java.util.ArrayList;

import entities.Entity;
import grid.Grid;
import javafx.scene.canvas.GraphicsContext;
import utils.Direction;

public abstract class BuildMode {
	public abstract Entity preview(int x, int y, Direction direction);
    public abstract void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y, Direction direction);
    public abstract String getBuildModeLabel();
}

