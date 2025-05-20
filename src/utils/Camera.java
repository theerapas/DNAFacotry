package utils;

import java.util.Set;

import javafx.scene.input.KeyCode;
import main.InputHandler;

public class Camera {

	private double cameraX; // Camera's top-left corner X
	private double cameraY; // Camera's top-left corner Y
	private static double CAMERA_SPEED; // Speed of camera movement
	private double screenWidth = Config.SCREEN_WIDTH; // Fixed screen width
	private double screenHeight = Config.SCREEN_HEIGHT; // Fixed screen height
	private double zoom = 1.0; // Default zoom level
	private static final double MIN_ZOOM = 0.5; // Minimum zoom level
	private static final double MAX_ZOOM = 2; // Maximum zoom level

	public Camera(double initialX, double initialY, double speed, InputHandler inputHandler, double zoom) {
		this.cameraX = initialX;
		this.cameraY = initialY;
		this.CAMERA_SPEED = speed;
		this.zoom = zoom;
	}

	public void moveCamera(double deltaX, double deltaY) {
		cameraX += deltaX;
		cameraY += deltaY;
	}

	public double getCameraX() {
		return cameraX;
	}

	public double getCameraY() {
		return cameraY;
	}

	public void setCameraX(double cameraX) {
		this.cameraX = cameraX;
	}

	public void setCameraY(double cameraY) {
		this.cameraY = cameraY;
	}

	public static double getCameraSpeed() {
		return CAMERA_SPEED;
	}

	public static void setCameraSpeed(double cameraSpeed) {
		CAMERA_SPEED = cameraSpeed;
	}

	public double getScreenWidth() {
		return screenWidth / zoom; // Adjust screen width based on zoom
	}

	public void setScreenWidth(double screenWidth) {
		this.screenWidth = screenWidth;
	}

	public double getScreenHeight() {
		return screenHeight / zoom; // Adjust screen height based on zoom
	}

	public void setScreenHeight(double screenHeight) {
		this.screenHeight = screenHeight;
	}

	public void update(InputHandler inputHandler) {
		// Update camera position based on input

		// Get camera movement from InputHandler
		double[] movement = inputHandler.getCameraMovement(CAMERA_SPEED);
		moveCamera(movement[0], movement[1]);

		double borderThreshold = 50; // Adjust this value as needed
		updateWithMouseBorder(inputHandler, borderThreshold);

		// Handle key "H" for camera reset
		if (inputHandler.isKeyPressed(KeyCode.H)) {
			cameraX = 0;
			cameraY = 0;
		}

		// Handle zooming
		if (inputHandler.getScrollDeltaY() > 0) {
			zoomIn();
		} else if (inputHandler.getScrollDeltaY() < 0) {
			zoomOut();
		}

		// later implement mouse camera movement

	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		// Get the center of the screen in world coordinates before zooming
		double centerX = cameraX + getScreenWidth() / (2);
		double centerY = cameraY + getScreenHeight() / (2);
		this.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom)); // Clamp zoom level
		// Adjust the camera position to keep the center consistent
		cameraX = centerX - getScreenWidth() / (2);
		cameraY = centerY - getScreenHeight() / (2);
	}

	public void zoomIn() {
		setZoom(zoom + 0.1);
	}

	public void zoomOut() {
		setZoom(zoom - 0.1);
	}

	public void updateWithMouseBorder(InputHandler inputHandler, double borderThreshold) {

		if (!inputHandler.isMouseOnScreen()) {
			return; // Do not update if the mouse is off the screen
		}
		double mouseX = inputHandler.getMouseX();
		double mouseY = inputHandler.getMouseY();

		// Ensure the mouse is within the screen bounds
		if (mouseX < 0 || mouseY < 0 || mouseX > screenWidth || mouseY > screenHeight) {
			return; // Exit if the mouse is out of bounds
		}

		// Calculate the center of the screen
		double centerX = screenWidth / 2;
		double centerY = screenHeight / 2;

		// Calculate the vector from the center to the mouse position
		double deltaX = mouseX - centerX;
		double deltaY = mouseY - centerY;

		// Check if the mouse is near the border
		if (Math.abs(deltaX) > centerX - borderThreshold || Math.abs(deltaY) > centerY - borderThreshold) {
			// Normalize the vector
			double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			double normalizedX = deltaX / magnitude;
			double normalizedY = deltaY / magnitude;

			// Move the camera in the direction of the vector
			moveCamera(normalizedX * CAMERA_SPEED, normalizedY * CAMERA_SPEED);
		}
	}
}
