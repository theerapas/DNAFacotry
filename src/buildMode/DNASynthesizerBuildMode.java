package buildMode;

import java.util.ArrayList;
import entities.DNASynthesizer;
import entities.Entity;
import grid.Grid;
import utils.Direction;
import utils.Game;
import utils.SoundManager;

public class DNASynthesizerBuildMode extends BuildMode {
	private DNASynthesizer.Mode currentMode = DNASynthesizer.Mode.MODE1;

	public void setMode(DNASynthesizer.Mode mode) {
		this.currentMode = mode;
	}

	@Override
	public Entity preview(int x, int y, Direction direction) {
		DNASynthesizer synthesizer = new DNASynthesizer(x, y, direction);
		synthesizer.setMode(currentMode);
		return synthesizer;
	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {
		var synthesizer = new DNASynthesizer(x, y, direction);
		synthesizer.setMode(currentMode);
		if (!Game.instance.canPlaceEntityAt(x, y, synthesizer)) {
			SoundManager.play(SoundManager.SOUND_FART);
			return;
		}
		entities.add(synthesizer);
		SoundManager.play(SoundManager.SOUND_POP);
	}

	@Override
	public String getBuildModeLabel() {
		return "DNA Synthesizer";
	}
}