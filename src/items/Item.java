package items;

import javafx.scene.image.Image;

public class Item {
	public enum ItemType {
		NUCLEOTIDE, TRAIT, LIFEFORM
	}

	private ItemType type;
	private String dnaCode; // "A", "AT", "BRAINMUSCLE", "HUMAN"
	private Image image;

	public Item(ItemType type, String dnaCode, Image image) {
		this.type = type;
		this.dnaCode = dnaCode;
		this.image = image;
	}

	public ItemType getType() {
		return type;
	}

	public String getDnaCode() {
		return dnaCode;
	}

	public Image getImage() {
		return image;
	}

	@Override
	public String toString() {
		return dnaCode;
	}
}
