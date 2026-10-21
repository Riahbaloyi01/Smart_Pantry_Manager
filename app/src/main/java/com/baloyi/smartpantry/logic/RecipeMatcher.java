package com.baloyi.smartpantry.logic;

import com.baloyi.smartpantry.model.PantryItem;
import com.baloyi.smartpantry.model.Recipe;
import com.baloyi.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeMatcher {

    // Returns only the recipes the user can make RIGHT NOW
    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, Double> available = buildAvailableMap(pantry);
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (canMake(recipe, available)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    public static boolean canMake(Recipe recipe, List<PantryItem> pantry) {
        return canMake(recipe, buildAvailableMap(pantry));
    }

    // The strict rule: EVERY ingredient must be present in at least the needed quantity
    private static boolean canMake(Recipe recipe, Map<String, Double> available) {
        if (recipe.getIngredients().isEmpty()) {
            return false;
        }
        for (RecipeIngredient needed : recipe.getIngredients()) {
            String key = makeKey(needed.getName(), needed.getUnit());
            double have = available.containsKey(key) ? available.get(key) : 0;
            double need = UnitConverter.toBase(needed.getQuantity(), needed.getUnit());
            if (have < need) {
                return false; // one missing or too-small ingredient rules the recipe out
            }
        }
        return true;
    }

    // Adds up the pantry per ingredient, so two rows of "Butter" count together
    private static Map<String, Double> buildAvailableMap(List<PantryItem> pantry) {
        Map<String, Double> map = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double qty = UnitConverter.toBase(item.getQuantity(), item.getUnit());
            double old = map.containsKey(key) ? map.get(key) : 0;
            map.put(key, old + qty);
        }
        return map;
    }

    // Same ingredient + same kind of unit = same key
    private static String makeKey(String name, String unit) {
        return IngredientNormalizer.normalize(name) + "|" + UnitConverter.getType(unit);
    }
}