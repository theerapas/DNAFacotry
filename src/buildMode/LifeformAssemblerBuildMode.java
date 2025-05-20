package buildMode;

import java.util.ArrayList;

import entities.Entity;
import entities.LifeformAssembler;
import grid.Grid;
import utils.Direction;
import utils.Game;
import utils.SoundManager;

public class LifeformAssemblerBuildMode extends BuildMode {

	@Override
	public Entity preview(int x, int y, Direction direction) {
		return new LifeformAssembler(x, y, direction);
	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {
		var assembler = new LifeformAssembler(x, y, direction);
		if (!Game.instance.canPlaceEntityAt(x, y, assembler)) {
			SoundManager.play(SoundManager.SOUND_FART);
			return;
		}
		entities.add(assembler);
		SoundManager.play(SoundManager.SOUND_POP);
	}

	@Override
	public String getBuildModeLabel() {
		return "Lifeform Assembler";
	}

}
