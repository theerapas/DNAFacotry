package items;

import java.util.*;

public class TraitCombiner {
    private static final Map<Set<String>, String> TRAIT_RECIPES = new HashMap<>();
    private static final Map<String, String> TRAIT_COLORS = new HashMap<>();

    static {
        TRAIT_RECIPES.put(Set.of("A", "T"), "BLOOD");
        TRAIT_RECIPES.put(Set.of("G", "C"), "MUSCLE");
        TRAIT_RECIPES.put(Set.of("T", "G"), "BRAIN");

        TRAIT_COLORS.put("BLOOD", "#ff6666");
        TRAIT_COLORS.put("MUSCLE", "#66cc66");
        TRAIT_COLORS.put("BRAIN", "#9999ff");
    }

    public static Item tryCombine(Item i1, Item i2) {
        if (i1 == null || i2 == null) return null;
        if (i1.getType() != Item.ItemType.NUCLEOTIDE || i2.getType() != Item.ItemType.NUCLEOTIDE) return null;

        Set<String> pair = Set.of(i1.getDnaCode(), i2.getDnaCode());
        String result = TRAIT_RECIPES.get(pair);

        if (result != null) {
            String color = TRAIT_COLORS.getOrDefault(result, "#aaaaaa");
            return new Item(Item.ItemType.TRAIT, result, color);
        }

        return null;
    }
}
