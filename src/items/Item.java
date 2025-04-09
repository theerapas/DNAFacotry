package items;

public class Item {
	public enum ItemType {
        NUCLEOTIDE, TRAIT, LIFEFORM
    }

    private ItemType type;
    private String dnaCode; // "A", "AT", "BRAINMUSCLE", "HUMAN"
    private String color;

    public Item(ItemType type, String dnaCode, String color) {
        this.type = type;
        this.dnaCode = dnaCode;
        this.color = color;
    }

    public ItemType getType() {
        return type;
    }

    public String getDnaCode() {
        return dnaCode;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return dnaCode;
    }
}
