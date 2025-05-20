package items;

import java.util.*;

public class LifeformCombiner {
	private static final Map<Set<String>, String> LIFEFORM_RECIPES = new HashMap<>();

	static {
		LIFEFORM_RECIPES.put(new HashSet<>(Arrays.asList("ORGAN_BRAIN", "ORGAN_HEART")), "HUMAN");
		LIFEFORM_RECIPES.put(new HashSet<>(Arrays.asList("ORGAN_BRAIN", "ORGAN_LUNGS")), "OCTOPUS");
		LIFEFORM_RECIPES.put(new HashSet<>(Arrays.asList("ORGAN_HEART", "ORGAN_LUNGS")), "WORM");
	}

	public static Item tryCombine(Item i1, Item i2) {
		if (i1 == null || i2 == null)
			return null;
		if (i1.getType() != Item.ItemType.ORGAN || i2.getType() != Item.ItemType.ORGAN)
			return null;

		Set<String> pair = new HashSet<>();
		pair.add(i1.getDnaCode());
		pair.add(i2.getDnaCode());

		String result = LIFEFORM_RECIPES.get(pair);
		if (result != null) {
			return new Item(Item.ItemType.LIFEFORM, result);
		}

		return null;
	}
}
