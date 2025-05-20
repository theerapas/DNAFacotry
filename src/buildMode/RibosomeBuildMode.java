package buildMode;

import java.util.ArrayList;
import entities.Ribosome;
import entities.Entity;
import grid.Grid;
import utils.Direction;
import utils.Game;
import utils.SoundManager;

public class RibosomeBuildMode extends BuildMode {
	private Ribosome.Mode currentMode = Ribosome.Mode.RIBOSOME1;

	public void setMode(Ribosome.Mode mode) {
		this.currentMode = mode;
	}

	@Override
	public Entity preview(int x, int y, Direction direction) {
		Ribosome ribosome = new Ribosome(x, y, direction);
		ribosome.setMode(currentMode);
		return ribosome;
	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {
		var ribosome = new Ribosome(x, y, direction);
		ribosome.setMode(currentMode);
		if (!Game.instance.canPlaceEntityAt(x, y, ribosome)) {
			SoundManager.play(SoundManager.SOUND_FART);
			return;
		}
		overlayEntities.add(ribosome);
		SoundManager.play(SoundManager.SOUND_POP);
	}

	@Override
	public String getBuildModeLabel() {
		return "Ribosome";
	}
}