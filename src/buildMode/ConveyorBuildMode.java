package buildMode;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;
import java.util.Map;

import entities.Conveyor;
import entities.Conveyor.ConveyorType;
import entities.Entity;
import grid.Grid;
import javafx.css.converter.StopConverter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Direction;
import utils.Game;
import utils.SoundManager;

public class ConveyorBuildMode extends BuildMode {
	
	@Override
	public Entity preview(int x, int y, Direction direction) {
		return new Conveyor(x, y, direction , ConveyorType.STRAIGHT); // This is not finished yet
	}

	@Override
	public void place(Grid grid, ArrayList<Entity> entities, ArrayList<Entity> overlayEntities, int x, int y,
			Direction direction) {
		
		Conveyor conveyor = new Conveyor(x, y, direction , ConveyorType.STRAIGHT); // This is not finished yet

		if (!Game.instance.canPlaceEntityAt(x, y, conveyor)) {
			SoundManager.play(SoundManager.SOUND_FART);
			return;
		}
		
		Map<String, String> lastPushMap = Game.instance.getItemMover().getLastPushMap(); // key = toX,toY → value = fromX,fromY
		
		Entity upEntity = Game.instance.getEntityAt(x, y-1);
		Entity downEntity = Game.instance.getEntityAt(x, y+1);
		Entity leftEntity = Game.instance.getEntityAt(x-1, y);
		Entity rightEntity = Game.instance.getEntityAt(x+1, y);
		
		Conveyor upConveyor = null;
		Conveyor downConveyor = null;
		Conveyor leftConveyor = null;
		Conveyor rightConveyor = null;
		
		String center = x+","+y;
		String top = x+","+(y-1);
		String bottom = x+","+(y+1);
		String left = (x-1)+","+y;
		String right = (x+1)+","+y;
		
		String toTop = lastPushMap.get(top);
		String toBottom = lastPushMap.get(bottom);
		String toLeft = lastPushMap.get(left);
		String toRight = lastPushMap.get(right);
		
		if (upEntity instanceof Conveyor) {
			upConveyor = (Conveyor) upEntity; 			
		} 
		if (downEntity instanceof Conveyor) {
			downConveyor = (Conveyor) downEntity; 			
		} 
		if (leftEntity instanceof Conveyor) {
			leftConveyor = (Conveyor) leftEntity; 			
		} 
		if (rightEntity instanceof Conveyor) {
			rightConveyor = (Conveyor) rightEntity; 			
		}
		
		
		
		ConveyorType thisType;
		
		
		
		if (direction == Direction.UP) {
			if (downConveyor != null && downConveyor.getDirection() == Direction.UP && (downConveyor.getType() == ConveyorType.STRAIGHT || downConveyor.getType() == ConveyorType.CURVE_LEFT_TO_TOP || downConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_TOP)) {
				lastPushMap.put(center, bottom);
				if (upConveyor != null && upConveyor.getDirection() == Direction.UP && toTop == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(top, center);
				} else if (leftConveyor != null && (rightConveyor == null || rightConveyor.getDirection() != Direction.RIGHT) && leftConveyor.getDirection() == Direction.LEFT && toLeft == null) {
					thisType = ConveyorType.CURVE_BOTTOM_TO_LEFT;
					lastPushMap.put(left, center);
				} else if (rightConveyor != null && (leftConveyor == null || leftConveyor.getDirection() != Direction.LEFT) && rightConveyor.getDirection() == Direction.RIGHT && toRight == null) {
					thisType = ConveyorType.CURVE_BOTTOM_TO_RIGHT;
					lastPushMap.put(right, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (upConveyor != null && toTop == null) {
						if (upConveyor.getDirection() == Direction.LEFT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_LEFT);
						else if (upConveyor.getDirection() == Direction.RIGHT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_RIGHT);
						lastPushMap.put(top, center);
					}
				}
			} else if (leftConveyor != null && (rightConveyor == null || (rightConveyor.getDirection() != Direction.LEFT || !(rightConveyor.getType() == ConveyorType.STRAIGHT || rightConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_LEFT || rightConveyor.getType() == ConveyorType.CURVE_TOP_TO_LEFT))) && leftConveyor.getDirection() == Direction.RIGHT && (leftConveyor.getType() == ConveyorType.STRAIGHT || leftConveyor.getType() == ConveyorType.CURVE_TOP_TO_RIGHT || leftConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_RIGHT)) {
				thisType = ConveyorType.CURVE_LEFT_TO_TOP;
				lastPushMap.put(center, left);
				if (upConveyor != null && toTop == null) {
					if (upConveyor.getDirection() == Direction.LEFT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_LEFT);
					else if (upConveyor.getDirection() == Direction.RIGHT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_RIGHT);
					lastPushMap.put(top, center);
				}
			} else if (rightConveyor != null && (leftConveyor == null || (leftConveyor.getDirection() != Direction.RIGHT || !(leftConveyor.getType() == ConveyorType.STRAIGHT || leftConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_RIGHT || leftConveyor.getType() == ConveyorType.CURVE_TOP_TO_RIGHT))) && rightConveyor.getDirection() == Direction.LEFT && (rightConveyor.getType() == ConveyorType.STRAIGHT || rightConveyor.getType() == ConveyorType.CURVE_TOP_TO_LEFT || rightConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_LEFT)) {
				thisType = ConveyorType.CURVE_RIGHT_TO_TOP;
				lastPushMap.put(center, right);
				if (upConveyor != null && toTop == null) {
					if (upConveyor.getDirection() == Direction.LEFT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_LEFT);
					else if (upConveyor.getDirection() == Direction.RIGHT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_RIGHT);
					lastPushMap.put(top, center);
				}
			} else {
				if (downConveyor != null && downConveyor.getType() == ConveyorType.STRAIGHT) {
					if (downConveyor.getDirection() == Direction.LEFT) downConveyor.setType(ConveyorType.CURVE_RIGHT_TO_TOP);
					else if (downConveyor.getDirection() == Direction.RIGHT) downConveyor.setType(ConveyorType.CURVE_LEFT_TO_TOP);
					lastPushMap.put(center, bottom);
					
				}
				if (upConveyor != null && upConveyor.getDirection() == Direction.UP && toTop == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(top, center);
				} else if (leftConveyor != null && (rightConveyor == null || rightConveyor.getDirection() != Direction.RIGHT) && leftConveyor.getDirection() == Direction.LEFT && toLeft == null) {
					thisType = ConveyorType.CURVE_BOTTOM_TO_LEFT;
					lastPushMap.put(left, center);
				} else if (rightConveyor != null && (leftConveyor == null || leftConveyor.getDirection() != Direction.LEFT) && rightConveyor.getDirection() == Direction.RIGHT && toRight == null) {
					thisType = ConveyorType.CURVE_BOTTOM_TO_RIGHT;	
					lastPushMap.put(right, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (upConveyor != null && toTop == null) {
						if (upConveyor.getDirection() == Direction.LEFT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_LEFT);
						else if (upConveyor.getDirection() == Direction.RIGHT) upConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_RIGHT);
						lastPushMap.put(top, center);
					}
				}
			}
		} else if (direction == Direction.RIGHT) {
			if (leftConveyor != null && leftConveyor.getDirection() == Direction.RIGHT && (leftConveyor.getType() == ConveyorType.STRAIGHT || leftConveyor.getType() == ConveyorType.CURVE_TOP_TO_RIGHT || leftConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_RIGHT)) {
				lastPushMap.put(center, left);
				if (rightConveyor != null && rightConveyor.getDirection() == Direction.RIGHT && toRight == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(right, center);
				} else if (upConveyor != null && (downConveyor == null || downConveyor.getDirection() != Direction.DOWN) && upConveyor.getDirection() == Direction.UP && toTop == null) {
					thisType = ConveyorType.CURVE_LEFT_TO_TOP;
					lastPushMap.put(top, center);
				} else if (downConveyor != null && (upConveyor == null || upConveyor.getDirection() != Direction.UP) && downConveyor.getDirection() == Direction.DOWN && toBottom == null) {
					thisType = ConveyorType.CURVE_LEFT_TO_BOTTOM;
					lastPushMap.put(bottom, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (rightConveyor != null && toRight == null) {
						if (rightConveyor.getDirection() == Direction.UP) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_TOP);
						else if (rightConveyor.getDirection() == Direction.DOWN) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_BOTTOM);
						lastPushMap.put(right, center);
					}
				}
			} else if (upConveyor != null && (downConveyor == null || (downConveyor.getDirection() != Direction.UP || !(downConveyor.getType() == ConveyorType.STRAIGHT || downConveyor.getType() == ConveyorType.CURVE_LEFT_TO_TOP || downConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_TOP))) && upConveyor.getDirection() == Direction.DOWN && (upConveyor.getType() == ConveyorType.STRAIGHT || upConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_BOTTOM || upConveyor.getType() == ConveyorType.CURVE_LEFT_TO_BOTTOM)) {
				thisType = ConveyorType.CURVE_TOP_TO_RIGHT;
				lastPushMap.put(center, top);
				if (rightConveyor != null && toRight == null) {
					if (rightConveyor.getDirection() == Direction.UP) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_TOP);
					else if (rightConveyor.getDirection() == Direction.DOWN) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_BOTTOM);
					lastPushMap.put(right, center);
				}
			} else if (downConveyor != null && (upConveyor == null || (upConveyor.getDirection() != Direction.DOWN || !(upConveyor.getType() == ConveyorType.STRAIGHT || upConveyor.getType() == ConveyorType.CURVE_LEFT_TO_BOTTOM || upConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_BOTTOM))) && downConveyor.getDirection() == Direction.UP && (downConveyor.getType() == ConveyorType.STRAIGHT || downConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_TOP || downConveyor.getType() == ConveyorType.CURVE_LEFT_TO_TOP)) {
				thisType = ConveyorType.CURVE_BOTTOM_TO_RIGHT;
				lastPushMap.put(center, bottom);
				if (rightConveyor != null && toRight == null) {
					if (rightConveyor.getDirection() == Direction.UP) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_TOP);
					else if (rightConveyor.getDirection() == Direction.DOWN) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_BOTTOM);
					lastPushMap.put(right, center);
				}
			} else {
				if (leftConveyor != null && leftConveyor.getType() == ConveyorType.STRAIGHT) {
					if (leftConveyor.getDirection() == Direction.UP) leftConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_RIGHT);
					else if (leftConveyor.getDirection() == Direction.DOWN) leftConveyor.setType(ConveyorType.CURVE_TOP_TO_RIGHT);
					lastPushMap.put(center, left);
				}
				
				if (rightConveyor != null && rightConveyor.getDirection() == Direction.RIGHT && toRight == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(right, center);
				} else if (upConveyor != null && (downConveyor == null || downConveyor.getDirection() != Direction.DOWN) && upConveyor.getDirection() == Direction.UP && toTop == null) {
					thisType = ConveyorType.CURVE_LEFT_TO_TOP;
					lastPushMap.put(top, center);
				} else if (downConveyor != null && (upConveyor == null || upConveyor.getDirection() != Direction.UP) && downConveyor.getDirection() == Direction.DOWN && toBottom == null) {
					thisType = ConveyorType.CURVE_LEFT_TO_BOTTOM;	
					lastPushMap.put(bottom, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (rightConveyor != null && toRight == null) {
						if (rightConveyor.getDirection() == Direction.UP) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_TOP);
						else if (rightConveyor.getDirection() == Direction.DOWN) rightConveyor.setType(ConveyorType.CURVE_LEFT_TO_BOTTOM);
						lastPushMap.put(right, center);
					}
				}
			}
		} else if (direction == Direction.DOWN) {
			if (upConveyor != null && upConveyor.getDirection() == Direction.DOWN && (upConveyor.getType() == ConveyorType.STRAIGHT || upConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_BOTTOM || upConveyor.getType() == ConveyorType.CURVE_LEFT_TO_BOTTOM)) {
				lastPushMap.put(center, top);
				if (downConveyor != null && downConveyor.getDirection() == Direction.DOWN && toBottom == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(bottom, center);
				} else if (rightConveyor != null && (leftConveyor == null || leftConveyor.getDirection() != Direction.LEFT) && rightConveyor.getDirection() == Direction.RIGHT && toRight == null) {
					thisType = ConveyorType.CURVE_TOP_TO_RIGHT;
					lastPushMap.put(right, center);
				} else if (leftConveyor != null && (rightConveyor == null || rightConveyor.getDirection() != Direction.RIGHT) && leftConveyor.getDirection() == Direction.LEFT && toLeft == null) {
					thisType = ConveyorType.CURVE_TOP_TO_LEFT;
					lastPushMap.put(left, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (downConveyor != null && toBottom == null) {
						if (downConveyor.getDirection() == Direction.RIGHT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_RIGHT);
						else if (downConveyor.getDirection() == Direction.LEFT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_LEFT);
						lastPushMap.put(bottom, center);
					}
				}
			} else if (rightConveyor != null && (leftConveyor == null || (leftConveyor.getDirection() != Direction.RIGHT || !(leftConveyor.getType() == ConveyorType.STRAIGHT || leftConveyor.getType() == ConveyorType.CURVE_TOP_TO_RIGHT || leftConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_RIGHT))) && rightConveyor.getDirection() == Direction.LEFT && (rightConveyor.getType() == ConveyorType.STRAIGHT || rightConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_LEFT || rightConveyor.getType() == ConveyorType.CURVE_TOP_TO_LEFT)) {
				thisType = ConveyorType.CURVE_RIGHT_TO_BOTTOM;
				lastPushMap.put(center, right);
				if (downConveyor != null && toBottom == null) {
					if (downConveyor.getDirection() == Direction.RIGHT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_RIGHT);
					else if (downConveyor.getDirection() == Direction.LEFT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_LEFT);
					lastPushMap.put(bottom, center);
				}
			} else if (leftConveyor != null && (rightConveyor == null || (rightConveyor.getDirection() != Direction.LEFT || !(rightConveyor.getType() == ConveyorType.STRAIGHT || rightConveyor.getType() == ConveyorType.CURVE_TOP_TO_LEFT || rightConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_LEFT))) && leftConveyor.getDirection() == Direction.RIGHT && (leftConveyor.getType() == ConveyorType.STRAIGHT || leftConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_RIGHT || leftConveyor.getType() == ConveyorType.CURVE_TOP_TO_RIGHT)) {
				thisType = ConveyorType.CURVE_LEFT_TO_BOTTOM;
				lastPushMap.put(center, left);
				if (downConveyor != null && toBottom == null) {
					if (downConveyor.getDirection() == Direction.RIGHT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_RIGHT);
					else if (downConveyor.getDirection() == Direction.LEFT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_LEFT);
					lastPushMap.put(bottom, center);
				}
			} else {
				if (upConveyor != null && upConveyor.getType() == ConveyorType.STRAIGHT) {
					if (upConveyor.getDirection() == Direction.RIGHT) upConveyor.setType(ConveyorType.CURVE_LEFT_TO_BOTTOM);
					else if (upConveyor.getDirection() == Direction.LEFT) upConveyor.setType(ConveyorType.CURVE_RIGHT_TO_BOTTOM);
					lastPushMap.put(center, top);
				}
				
				if (downConveyor != null && downConveyor.getDirection() == Direction.DOWN && toBottom == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(bottom, center);
				} else if (rightConveyor != null && (leftConveyor == null || leftConveyor.getDirection() != Direction.LEFT) && rightConveyor.getDirection() == Direction.RIGHT && toRight == null) {
					thisType = ConveyorType.CURVE_TOP_TO_RIGHT;
					lastPushMap.put(right, center);
				} else if (leftConveyor != null && (rightConveyor == null || rightConveyor.getDirection() != Direction.RIGHT) && leftConveyor.getDirection() == Direction.LEFT && toLeft == null) {
					thisType = ConveyorType.CURVE_TOP_TO_LEFT;	
					lastPushMap.put(left, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (downConveyor != null && toBottom == null) {
						if (downConveyor.getDirection() == Direction.RIGHT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_RIGHT);
						else if (downConveyor.getDirection() == Direction.LEFT) downConveyor.setType(ConveyorType.CURVE_TOP_TO_LEFT);
						lastPushMap.put(bottom, center);
					}
				}
			}
		} else {
			if (rightConveyor != null && rightConveyor.getDirection() == Direction.LEFT && (rightConveyor.getType() == ConveyorType.STRAIGHT || rightConveyor.getType() == ConveyorType.CURVE_BOTTOM_TO_LEFT || rightConveyor.getType() == ConveyorType.CURVE_TOP_TO_LEFT)) {
				lastPushMap.put(center, right);
				if (leftConveyor != null && leftConveyor.getDirection() == Direction.LEFT && toLeft == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(left, center);
				} else if (downConveyor != null && (upConveyor == null || upConveyor.getDirection() != Direction.UP) && downConveyor.getDirection() == Direction.DOWN && toBottom == null) {
					thisType = ConveyorType.CURVE_RIGHT_TO_BOTTOM;
					lastPushMap.put(bottom, center);
				} else if (upConveyor != null && (downConveyor == null || downConveyor.getDirection() != Direction.DOWN) && upConveyor.getDirection() == Direction.UP && toTop == null) {
					thisType = ConveyorType.CURVE_RIGHT_TO_TOP;					
					lastPushMap.put(top, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (leftConveyor != null && toLeft == null) {
						if (leftConveyor.getDirection() == Direction.DOWN) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_BOTTOM);
						else if (leftConveyor.getDirection() == Direction.UP) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_TOP);
						lastPushMap.put(left, center);
					}
				}
			} else if (downConveyor != null && (upConveyor == null || (upConveyor.getDirection() != Direction.DOWN || !(upConveyor.getType() == ConveyorType.STRAIGHT || upConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_BOTTOM || upConveyor.getType() == ConveyorType.CURVE_LEFT_TO_BOTTOM))) && downConveyor.getDirection() == Direction.UP && (downConveyor.getType() == ConveyorType.STRAIGHT || downConveyor.getType() == ConveyorType.CURVE_LEFT_TO_TOP || downConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_TOP)) {
				thisType = ConveyorType.CURVE_BOTTOM_TO_LEFT;
				lastPushMap.put(center, bottom);
				if (leftConveyor != null && toLeft == null) {
					if (leftConveyor.getDirection() == Direction.DOWN) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_BOTTOM);
					else if (leftConveyor.getDirection() == Direction.UP) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_TOP);
					lastPushMap.put(left, center);
				}
			} else if (upConveyor != null && (downConveyor == null || (downConveyor.getDirection() != Direction.UP || !(downConveyor.getType() == ConveyorType.STRAIGHT || downConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_TOP || downConveyor.getType() == ConveyorType.CURVE_LEFT_TO_TOP))) && upConveyor.getDirection() == Direction.DOWN && (upConveyor.getType() == ConveyorType.STRAIGHT || upConveyor.getType() == ConveyorType.CURVE_LEFT_TO_BOTTOM || upConveyor.getType() == ConveyorType.CURVE_RIGHT_TO_BOTTOM)) {
				thisType = ConveyorType.CURVE_TOP_TO_LEFT;
				lastPushMap.put(center, top);
				if (leftConveyor != null && toLeft == null) {
					if (leftConveyor.getDirection() == Direction.DOWN) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_BOTTOM);
					else if (leftConveyor.getDirection() == Direction.UP) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_TOP);
					lastPushMap.put(left, center);
				}
			} else {
				if (rightConveyor != null && rightConveyor.getType() == ConveyorType.STRAIGHT) {
					if (rightConveyor.getDirection() == Direction.DOWN) rightConveyor.setType(ConveyorType.CURVE_TOP_TO_LEFT);
					else if (rightConveyor.getDirection() == Direction.UP) rightConveyor.setType(ConveyorType.CURVE_BOTTOM_TO_LEFT);
					lastPushMap.put(center, right);
				}
				
				if (leftConveyor != null && leftConveyor.getDirection() == Direction.LEFT && toLeft == null){
					thisType = ConveyorType.STRAIGHT;
					lastPushMap.put(left, center);
				} else if (downConveyor != null && (upConveyor == null || upConveyor.getDirection() != Direction.UP) && downConveyor.getDirection() == Direction.DOWN && toBottom == null) {
					thisType = ConveyorType.CURVE_RIGHT_TO_BOTTOM;
					lastPushMap.put(bottom, center);
				} else if (upConveyor != null && (downConveyor == null || downConveyor.getDirection() != Direction.DOWN) && upConveyor.getDirection() == Direction.UP && toTop == null) {
					thisType = ConveyorType.CURVE_RIGHT_TO_TOP;	
					lastPushMap.put(top, center);
				} else {
					thisType = ConveyorType.STRAIGHT;
					if (leftConveyor != null && toLeft == null) {
						if (leftConveyor.getDirection() == Direction.DOWN) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_BOTTOM);
						else if (leftConveyor.getDirection() == Direction.UP) leftConveyor.setType(ConveyorType.CURVE_RIGHT_TO_TOP);
						lastPushMap.put(left, center);
					}
				}
			}
		}
		
		conveyor.setType(thisType);
		entities.add(conveyor);
		return;
		
	}

	@Override
	public String getBuildModeLabel() {
		return "Conveyor";
	}

}
