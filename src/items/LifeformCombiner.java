package items;

import java.util.*;

public class LifeformCombiner {
    private static final Map<Set<String>, String> LIFEFORM_RECIPES = new HashMap<>();
    private static final Map<String, String> LIFEFORM_COLORS = new HashMap<>();

    static {
        LIFEFORM_RECIPES.put(new HashSet<>(Arrays.asList("BLOOD", "MUSCLE")), "HUMAN");
        LIFEFORM_RECIPES.put(new HashSet<>(Arrays.asList("MUSCLE", "BRAIN")), "BEAST");
        LIFEFORM_RECIPES.put(new HashSet<>(Arrays.asList("BLOOD", "BRAIN")), "ALIEN");

        LIFEFORM_COLORS.put("HUMAN", "#f4e842");
        LIFEFORM_COLORS.put("BEAST", "#f47842");
        LIFEFORM_COLORS.put("ALIEN", "#8e42f4");
    }

    public static Item tryCombine(Item i1, Item i2) {
        if (i1 == null || i2 == null) return null;
        if (i1.getType() != Item.ItemType.TRAIT || i2.getType() != Item.ItemType.TRAIT) return null;

        if (i1.getDnaCode().equals(i2.getDnaCode())) return null;

        Set<String> pair = new HashSet<>();
        pair.add(i1.getDnaCode());
        pair.add(i2.getDnaCode());

        String result = LIFEFORM_RECIPES.get(pair);
        if (result != null) {
            String color = LIFEFORM_COLORS.getOrDefault(result, "#aaaaaa");
            return new Item(Item.ItemType.LIFEFORM, result, color);
        }

        return null;
    }
}
