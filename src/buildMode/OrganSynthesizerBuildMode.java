package buildMode;

import java.util.ArrayList;

import entities.OrganSynthesizer;
import entities.Entity;
import grid.Grid;
import utils.Direction;
import utils.Game;
import utils.SoundManager;

public class OrganSynthesizerBuildMode extends BuildMode {

	@Override
	public Entity preview(int x, int y, Direction direction) {
		return new OrganSynthesizer(x, y, direction);
	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {
		var synthesizer = new OrganSynthesizer(x, y, direction);
		if (!Game.instance.canPlaceEntityAt(x, y, synthesizer)) {
			SoundManager.play(SoundManager.SOUND_FART);
			return;
		}
		entities.add(synthesizer);
		SoundManager.play(SoundManager.SOUND_POP);
	}

	@Override
	public String getBuildModeLabel() {
		return "Organ Synthesizer";
	}
}