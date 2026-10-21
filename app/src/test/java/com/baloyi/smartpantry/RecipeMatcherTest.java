package com.baloyi.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.baloyi.smartpantry.logic.IngredientNormalizer;
import com.baloyi.smartpantry.logic.RecipeMatcher;
import com.baloyi.smartpantry.model.PantryItem;
import com.baloyi.smartpantry.model.Recipe;
import com.baloyi.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RecipeMatcherTest {

    // Cheese Omelette: 3 eggs, 50 g cheese, 1 tsp salt
    private Recipe omelette() {
        Recipe r = new Recipe(1, "Cheese Omelette", "steps");
        r.addIngredient(new RecipeIngredient("egg", 3, "pcs"));
        r.addIngredient(new RecipeIngredient("cheese", 50, "g"));
        r.addIngredient(new RecipeIngredient("salt", 1, "tsp"));
        return r;
    }

    private PantryItem item(String name, double qty, String unit) {
        return new PantryItem(1, name, qty, unit, "");
    }

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(item("Eggs", 6, "pcs"));
        pantry.add(item("Cheese", 200, "g"));
        pantry.add(item("Salt", 500, "g"));
        assertTrue(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void oneIngredientMissing_recipeIsNotSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(item("Eggs", 6, "pcs"));
        pantry.add(item("Cheese", 200, "g"));
        // salt is missing
        assertFalse(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void quantityTooSmall_recipeIsNotSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(item("Eggs", 2, "pcs")); // needs 3
        pantry.add(item("Cheese", 200, "g"));
        pantry.add(item("Salt", 100, "g"));
        assertFalse(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void pluralAndCapitalsStillMatch() {
        Recipe r = new Recipe(2, "Tomato Soup", "steps");
        r.addIngredient(new RecipeIngredient("tomato", 4, "pcs"));

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(item("TOMATOES ", 5, "pcs"));
        assertTrue(RecipeMatcher.canMake(r, pantry));
    }

    @Test
    public void differentUnitsAreConverted() {
        Recipe r = new Recipe(3, "Pancakes", "steps");
        r.addIngredient(new RecipeIngredient("flour", 150, "g"));

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(item("Flour", 1, "kg"));
        assertTrue(RecipeMatcher.canMake(r, pantry));
    }

    @Test
    public void duplicatePantryRowsAreAddedTogether() {
        Recipe r = new Recipe(4, "Mashed Potatoes", "steps");
        r.addIngredient(new RecipeIngredient("butter", 30, "g"));

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(item("Butter", 20, "g"));
        pantry.add(item("Butter", 20, "g"));
        assertTrue(RecipeMatcher.canMake(r, pantry));
    }

    @Test
    public void emptyPantry_noRecipesSuggested() {
        List<Recipe> all = new ArrayList<>();
        all.add(omelette());
        assertTrue(RecipeMatcher.getSuggestedRecipes(all, new ArrayList<>()).isEmpty());
    }

    @Test
    public void normalizerHandlesCommonPlurals() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
        assertEquals("egg", IngredientNormalizer.normalize("Eggs"));
        assertEquals("potato", IngredientNormalizer.normalize("potatoes"));
        assertEquals("berry", IngredientNormalizer.normalize("Berries"));
        assertEquals("cheese", IngredientNormalizer.normalize("Cheese"));
        assertEquals("maize meal", IngredientNormalizer.normalize("  Maize   Meal "));
    }
}