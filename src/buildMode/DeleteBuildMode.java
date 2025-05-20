package buildMode;

import java.util.ArrayList;

import entities.DeliveryZone;
import entities.Entity;
import entities.Tunnel;
import entities.TunnelEnd;
import grid.Grid;
import items.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Direction;
import utils.Game;
import utils.ItemMover;
import utils.SoundManager;

public class DeleteBuildMode extends BuildMode {

	@Override
	public Entity preview(int x, int y, Direction direction) {
		return null;
	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {

		Entity entityToDelete = Game.instance.getEntityAt(x, y);
		Entity overlayToDelete = Game.instance.getOverlayEntityAt(x, y);

		boolean deletedSomething = false;

		// Handle overlay entity first (like Tunnel)
		if (overlayToDelete != null && !(overlayToDelete instanceof DeliveryZone)) {
			if (overlayToDelete instanceof TunnelEnd) {
				// Clear the exit from any tunnel that points here
				for (Entity base : overlayEntities) {
					if (base instanceof Tunnel tunnel) {
						tunnel.clearExitIfMatches(x, y);
					}
				}
			}
			overlayEntities.remove(overlayToDelete);
			deletedSomething = true;
		}

		// Handle base entity (except DeliveryZone)
		if (entityToDelete != null ) {
			entities.remove(entityToDelete);
			deletedSomething = true;
		}

		if (deletedSomething) {
			// Clear any item on tile
			Game.instance.clearItemAt(x, y);
			Game.instance.getItemMover().forgetOwnership(x, y);

//			System.out.println("Removed entity at (" + x + "," + y + ")");
			SoundManager.play(SoundManager.SOUND_FART);
		}
	}

	@Override
	public String getBuildModeLabel() {
		return "Delete (X)";
	}

}
