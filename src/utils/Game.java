package utils;

import main.GameState;

public class Game {
	public static GameState instance;

	public ItemMover getItemMover() {
		return ((GameState) Game.instance).getItemMover();
	}

}
