package items;

public class Item {
	public enum ItemType {
		NUCLEOTIDE, TRAIT, LIFEFORM, PROTEIN, ORGAN
	}

	private ItemType type;
	private String dnaCode; // "A", "AT", "BRAINMUSCLE", "HUMAN"
	private double progress; // 0.0 - 1.0

	public Item(ItemType type, String dnaCode) {
		this.type = type;
		this.dnaCode = dnaCode;
		this.progress = 0.0;
	}

	public ItemType getType() {
		return type;
	}

	public String getDnaCode() {
		return dnaCode;
	}

	@Override
	public String toString() {
		return dnaCode;
	}
	
	public void setProgress(double progress) {
		if(progress < 0.0) {
			this.progress = 0.0;
			return;
		}
		if(progress > 1.0) {
			this.progress = 1.0;
			return;
		}
		this.progress = progress;
	}
	
	public double getProgress() {
		return progress;
	}

}
