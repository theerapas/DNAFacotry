package buildMode;

import java.util.ArrayList;

import entities.Entity;
import entities.Extractor;
import grid.Grid;
import grid.Tile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Direction;
import utils.Game;
import utils.SoundManager;

public class ExtractorBuildMode extends BuildMode {
	@Override
	public Entity preview(int x, int y, Direction direction) {
		Tile tile = Game.instance.getGrid().getTile(x, y);
		if (tile != null && tile.isResource()) {
			return new Extractor(x, y, direction, tile.getResource());
		}
		return null;
	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {
		Tile tile = grid.getTile(x, y);
		if (tile != null && tile.isResource()) {
			String code = tile.getResource();
			var extractor = new Extractor(x, y, direction, code);
			if (!Game.instance.canPlaceEntityAt(x, y, extractor)) {
				SoundManager.play(SoundManager.SOUND_FART);
				return;
			}
			entities.add(extractor);
			SoundManager.play(SoundManager.SOUND_POP);
		}
	}

	@Override
	public String getBuildModeLabel() {
		return "Extractor";
	}
}