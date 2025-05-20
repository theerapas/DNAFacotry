package items;

import java.util.Random;

public class NucleotideFactory {
	private static final String[] BASES = { "A", "T", "G", "C" };

	public static Item randomNucleotide() {
		Random rand = new Random();
		int i = rand.nextInt(BASES.length);
		return new Item(Item.ItemType.NUCLEOTIDE, BASES[i]);
	}

	public static Item getNucleotide(String base) {
		for (int i = 0; i < BASES.length; i++) {
			if (BASES[i].equals(base)) {
				return new Item(Item.ItemType.NUCLEOTIDE, base);
			}
		}
		return null;
	}
}
