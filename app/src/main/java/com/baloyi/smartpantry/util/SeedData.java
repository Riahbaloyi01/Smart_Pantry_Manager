package com.baloyi.smartpantry.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SeedData {

    // Called once, when the database is first created
    public static void seed(SQLiteDatabase db) {
        add(db, "Scrambled Eggs",
                "1. Crack the eggs into a bowl, add salt and whisk.\n2. Melt the butter in a pan.\n3. Pour in the eggs and stir gently until just set.",
                "egg|3|pcs", "butter|10|g", "salt|1|tsp");

        add(db, "Cheese Omelette",
                "1. Whisk the eggs with salt.\n2. Melt butter in a pan and pour in the eggs.\n3. Add the grated cheese, fold the omelette and cook for 1 minute.",
                "egg|3|pcs", "cheese|50|g", "butter|10|g", "salt|1|tsp");

        add(db, "Pancakes",
                "1. Mix flour, sugar, egg and milk into a smooth batter.\n2. Melt a little butter in a pan.\n3. Pour in a ladle of batter and cook until golden on both sides.",
                "flour|150|g", "milk|200|ml", "egg|1|pcs", "sugar|1|tbsp", "butter|10|g");

        add(db, "French Toast",
                "1. Whisk the eggs, milk and sugar together.\n2. Dip each slice of bread into the mixture.\n3. Fry in melted butter until golden on both sides.",
                "bread|4|pcs", "egg|2|pcs", "milk|100|ml", "butter|10|g", "sugar|1|tbsp");

        add(db, "Cheese Toastie",
                "1. Butter the outside of the bread slices.\n2. Place the cheese between them.\n3. Toast in a pan until golden and the cheese has melted.",
                "bread|2|pcs", "cheese|40|g", "butter|10|g");

        add(db, "Tomato Pasta",
                "1. Boil the pasta in salted water until soft.\n2. Fry chopped onion and garlic in oil.\n3. Add chopped tomatoes and simmer for 10 minutes.\n4. Mix the sauce through the pasta.",
                "pasta|200|g", "tomato|4|pcs", "onion|1|pcs", "garlic|2|pcs", "oil|2|tbsp", "salt|1|tsp");

        add(db, "Garlic Butter Pasta",
                "1. Boil the pasta in salted water until soft and drain.\n2. Melt the butter and fry crushed garlic for 1 minute.\n3. Toss the pasta in the garlic butter.",
                "pasta|200|g", "butter|30|g", "garlic|3|pcs", "salt|1|tsp");

        add(db, "Cheese Pasta",
                "1. Boil the pasta in salted water and drain.\n2. Melt butter, add milk and the grated cheese, and stir until smooth.\n3. Mix the sauce through the pasta.",
                "pasta|200|g", "cheese|100|g", "milk|100|ml", "butter|20|g", "salt|1|tsp");

        add(db, "Egg Fried Rice",
                "1. Use cooked, cooled rice.\n2. Fry chopped onion in oil.\n3. Push to the side, scramble the eggs, then mix in the rice and salt.",
                "rice|200|g", "egg|2|pcs", "onion|1|pcs", "oil|2|tbsp", "salt|1|tsp");

        add(db, "Rice and Beans",
                "1. Cook the rice until soft.\n2. Fry chopped onion in oil, then add the beans and salt.\n3. Simmer for 10 minutes and serve over the rice.",
                "rice|200|g", "beans|200|g", "onion|1|pcs", "oil|1|tbsp", "salt|1|tsp");

        add(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with butter, milk and salt until smooth.",
                "potato|500|g", "butter|30|g", "milk|50|ml", "salt|1|tsp");

        add(db, "Roast Potatoes",
                "1. Heat the oven to 200 degrees Celsius.\n2. Cut the potatoes into chunks and toss in oil and salt.\n3. Roast for 40 minutes, turning once, until crispy.",
                "potato|600|g", "oil|3|tbsp", "salt|1|tsp");

        add(db, "Tomato Soup",
                "1. Fry chopped onion and garlic in oil.\n2. Add chopped tomatoes and salt and simmer for 20 minutes.\n3. Blend until smooth.",
                "tomato|6|pcs", "onion|1|pcs", "garlic|2|pcs", "oil|1|tbsp", "salt|1|tsp");

        add(db, "Tomato and Onion Omelette",
                "1. Fry sliced onion and tomato in oil for 3 minutes.\n2. Pour in the whisked eggs and salt.\n3. Cook until set and fold in half.",
                "egg|3|pcs", "tomato|1|pcs", "onion|1|pcs", "oil|1|tbsp", "salt|1|tsp");

        add(db, "Pap and Tomato Gravy",
                "1. Boil water with salt and stir in the maize meal until thick (pap).\n2. Fry onion in oil, add chopped tomatoes and simmer into a gravy.\n3. Serve the gravy over the pap.",
                "maize meal|250|g", "tomato|3|pcs", "onion|1|pcs", "oil|1|tbsp", "salt|1|tsp");

        add(db, "Chicken Stir-fry",
                "1. Cut the chicken, onion and carrots into strips.\n2. Fry the chicken in hot oil until cooked through.\n3. Add the vegetables and salt and stir-fry for 5 minutes.",
                "chicken|300|g", "onion|1|pcs", "carrot|2|pcs", "oil|2|tbsp", "salt|1|tsp");

        add(db, "Banana Pancakes",
                "1. Mash the bananas in a bowl.\n2. Whisk in the eggs and flour.\n3. Cook spoonfuls in a pan until golden on both sides.",
                "banana|2|pcs", "egg|2|pcs", "flour|50|g");
    }

    // Saves one recipe and its ingredients. Each ingredient is written as "name|quantity|unit"
    private static void add(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues recipe = new ContentValues();
        recipe.put("name", name);
        recipe.put("steps", steps);
        long recipeId = db.insert("recipes", null, recipe);

        for (String line : ingredients) {
            String[] parts = line.split("\\|");
            ContentValues ing = new ContentValues();
            ing.put("recipe_id", recipeId);
            ing.put("name", parts[0]);
            ing.put("quantity", Double.parseDouble(parts[1]));
            ing.put("unit", parts[2]);
            db.insert("recipe_ingredients", null, ing);
        }
    }
}