package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import ui.Toolbar;
import javafx.scene.input.ScrollEvent;
//import utils.BuildMode;
import buildMode.*;
import utils.Config;
import utils.Direction;
import utils.Game;
import entities.DNASynthesizer;
import entities.Ribosome;

public class InputHandler {

	private double mouseX, mouseY;
	private boolean mouseOnScreen = true;
	private Set<KeyCode> pressedKeys = new HashSet<>();
	private Set<MouseButton> pressedMouseButtons = new HashSet<>();
	private BuildMode buildMode = new ConveyorBuildMode(); // Default build mode
	private Direction direction = Direction.RIGHT;
	private Map<KeyCode, Runnable> keyBindings = new HashMap<>();
	private Map<MouseButton, Runnable> mouseBindings = new HashMap<>(); // For future use
	private ArrayList<ArrayList<Double>> clickPos = new ArrayList<>(); // Store mouse click coordinates
	private Toolbar toolbar;
	private double scrollDeltaY = 0; // store scroll mouse wheel input

	public InputHandler() {
		
		// Initialize key bindings
		keyBindings.put(KeyCode.DIGIT1, () -> buildMode = new ConveyorBuildMode());
		keyBindings.put(KeyCode.DIGIT2, () -> buildMode = new TunnelBuildMode());
		keyBindings.put(KeyCode.DIGIT3, () -> buildMode = new ExtractorBuildMode());
		keyBindings.put(KeyCode.R, this::rotate);
//		keyBindings.put(KeyCode.DIGIT4, () -> buildMode = new DNACombinerBuildMode());
		keyBindings.put(KeyCode.DIGIT4, () -> buildMode = new DNASynthesizerBuildMode());
		keyBindings.put(KeyCode.DIGIT5, () -> buildMode = new RibosomeBuildMode());
		keyBindings.put(KeyCode.DIGIT6, () -> buildMode = new OrganSynthesizerBuildMode());
		keyBindings.put(KeyCode.DIGIT7, () -> buildMode = new LifeformAssemblerBuildMode());
		keyBindings.put(KeyCode.X, () -> buildMode = new DeleteBuildMode());

		// DNA Synthesizer and Ribosome mode selection
		keyBindings.put(KeyCode.F1, () -> {
			if (buildMode instanceof DNASynthesizerBuildMode) {
				((DNASynthesizerBuildMode) buildMode).setMode(DNASynthesizer.Mode.MODE1);
			}
			if (buildMode instanceof RibosomeBuildMode) {
				((RibosomeBuildMode) buildMode).setMode(Ribosome.Mode.RIBOSOME1);
			}
		});
		keyBindings.put(KeyCode.F2, () -> {
			if (buildMode instanceof DNASynthesizerBuildMode) {
				((DNASynthesizerBuildMode) buildMode).setMode(DNASynthesizer.Mode.MODE2);
			}
			if (buildMode instanceof RibosomeBuildMode) {
				((RibosomeBuildMode) buildMode).setMode(Ribosome.Mode.RIBOSOME2);
			}
		});
		keyBindings.put(KeyCode.F3, () -> {
			if (buildMode instanceof DNASynthesizerBuildMode) {
				((DNASynthesizerBuildMode) buildMode).setMode(DNASynthesizer.Mode.MODE3);
			}
		});
		
		this.mouseX = Config.SCREEN_WIDTH/2;
		this.mouseY = Config.SCREEN_HEIGHT/2;
	}

	public InputHandler(InputHandler inputHandler) {
		this.mouseX = inputHandler.mouseX;
		this.mouseY = inputHandler.mouseY;
		this.mouseOnScreen = inputHandler.mouseOnScreen;
		this.pressedKeys = new HashSet<>(inputHandler.pressedKeys);
		this.pressedMouseButtons = new HashSet<>(inputHandler.pressedMouseButtons);
		this.buildMode = inputHandler.buildMode;
		this.direction = inputHandler.direction;
		this.keyBindings = new HashMap<>(inputHandler.keyBindings);
		this.mouseBindings = new HashMap<>(inputHandler.mouseBindings);
		this.clickPos = new ArrayList<>(inputHandler.clickPos);
	}

	public Set<KeyCode> getPressedKeys() {
		return pressedKeys;
	}

	public boolean isKeyPressed(KeyCode key) {
		return pressedKeys.contains(key);
	}

	public void setKeyPressed(KeyCode keycode, boolean pressed) {
		if (pressed) {
			Runnable action = keyBindings.get(keycode);
			if (action != null) {
				action.run();
			}
			pressedKeys.add(keycode);
		} else {
			pressedKeys.remove(keycode);
		}
	}

	public void addClickPos(double mouseX, double mouseY) {
		ArrayList<Double> pos = new ArrayList<>();
		pos.add(mouseX);
		pos.add(mouseY);
		clickPos.add(pos);
		if (toolbar != null) {
			toolbar.hideAllSubmenus();
		}
	}

	public void setMouseX(double x) {
		this.mouseX = x;
	}

	public void setMouseY(double y) {
		this.mouseY = y;
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	public boolean isMouseOnScreen() {
		return mouseOnScreen;
	}

	public void setMouseOnScreen(boolean mouseOnScreen) {
		this.mouseOnScreen = mouseOnScreen;
	}

	public boolean isMouseButtonPressed(MouseButton button) {
		return pressedMouseButtons.contains(button);
	}

	public void setMouseButtonPressed(MouseButton button, boolean pressed) {
		if (pressed) {
			pressedMouseButtons.add(button);
		} else {
			pressedMouseButtons.remove(button);
		}
	}

	public Set<MouseButton> getPressedMouseButtons() {
		return pressedMouseButtons;
	}

	public BuildMode getBuildMode() {
		return buildMode;
	}

	public void setBuildMode(BuildMode buildMode) {
		this.buildMode = buildMode;
		if (toolbar != null) {
			toolbar.hideAllSubmenus();
		}
	}

	public void updateInputState() {
		pressedMouseButtons.clear();
		pressedKeys.clear();
	}

	public double[] getCameraMovement(double cameraSpeed) {
		double deltaX = 0, deltaY = 0;
		if (isKeyPressed(KeyCode.W)) {
			deltaY -= cameraSpeed;
		}
		if (isKeyPressed(KeyCode.S)) {
			deltaY += cameraSpeed;
		}
		if (isKeyPressed(KeyCode.A)) {
			deltaX -= cameraSpeed;
		}
		if (isKeyPressed(KeyCode.D)) {
			deltaX += cameraSpeed;
		}
		if (deltaX != 0 && deltaY != 0) {
			deltaX = deltaX / Math.sqrt(2);
			deltaY = deltaY / Math.sqrt(2);
		}
		return new double[] { deltaX, deltaY };
	}

	public void rotate() {
		direction = direction.rotateClockwise();
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getDirectionLabel() {
		return switch (direction) {
		case UP -> "↑ UP";
		case RIGHT -> "→ RIGHT";
		case DOWN -> "↓ DOWN";
		case LEFT -> "← LEFT";
		};
	}

	public ArrayList<ArrayList<Double>> getClickPos() {
		return clickPos;
	}

	public void clearAllInputs() {
		clickPos.clear();
		scrollDeltaY = 0;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	public void handleScroll(ScrollEvent event) {
		scrollDeltaY += event.getDeltaY();
	}

	public double getScrollDeltaY() {
		return scrollDeltaY;
	}
}
