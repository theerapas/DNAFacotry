package buildMode;

import java.util.ArrayList;

import entities.Conveyor;
import entities.Conveyor.ConveyorType;
import entities.Entity;
import entities.Tunnel;
import entities.TunnelEnd;
import grid.Grid;
import utils.Direction;
import utils.Game;
import utils.SoundManager;

public class TunnelBuildMode extends BuildMode {

	private Tunnel pendingTunnelStart = null;

	@Override
	public Entity preview(int x, int y, Direction direction) {
		if (pendingTunnelStart == null) {
			return new Tunnel(x, y, direction, -1, -1);
		} else {
			return new TunnelEnd(x, y, direction);
		}

	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {
		if (pendingTunnelStart == null) {
			// Step 1: Place entrance
			var tunnel = new Tunnel(x, y, direction, -1, -1);
			if (!Game.instance.canPlaceEntityAt(x, y, tunnel)) {
				SoundManager.play(SoundManager.SOUND_FART);
				return;
			}

			pendingTunnelStart = tunnel;
			overlayEntities.add(tunnel); // Tunnel on overlay

			entities.add(new Conveyor(x, y, direction, ConveyorType.STRAIGHT)); // Conveyor on base (This is not
																				// finished yet)

		} else {
			// Step 2: Place exit
			var tunnelEnd = new TunnelEnd(x, y, direction);
			if (!Game.instance.canPlaceEntityAt(x, y, tunnelEnd)) {
				SoundManager.play(SoundManager.SOUND_FART);
				return;
			}
			pendingTunnelStart.setExit(x, y);
			overlayEntities.add(tunnelEnd); // Tunnel end on overlay

			entities.add(new Conveyor(x, y, direction, ConveyorType.STRAIGHT)); // Conveyor on base (This is not
																				// finished yet)

			pendingTunnelStart = null;
		}
	}

	@Override
	public String getBuildModeLabel() {
		return "Tunnel";
	}

}
