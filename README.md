# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user keeps track of the ingredients they already have at home (their "pantry"), and the app suggests only the recipes they can cook right now, with no shopping trip needed.

Built by Nonhlanhla Baloyi for Mobile App Development 700 (Richfield Graduate Institute of Technology).

## Features

- **Pantry management:** add, edit and delete ingredients (name, quantity, unit and an optional expiry date), with input validation.
- **Pantry list:** a RecyclerView with a custom adapter, backed by the database. Items that expire within 3 days (or have expired) are shown in red.
- **Recipe collection:** 17 recipes are pre-loaded into the database the first time the app runs.
- **Suggested Recipes:** shows only the recipes the user can make with the current pantry, using the strict-matching rule below. If nothing matches, it shows a "No recipes match your pantry yet" message.
- **Recipe detail:** the full ingredient list and method for a selected recipe.
- **Settings:** a switch for expiry alerts and a default unit for new ingredients, saved with SharedPreferences.
- **Bottom navigation** between Pantry, Recipes and Settings.

## The strict-matching rule

A recipe is suggested only if every one of its ingredients is in the pantry in at least the required quantity. If a recipe needs 5 ingredients and the pantry has 4, it is not suggested. The matching copes with everyday differences:

- Names are normalised, so "Tomatoes", "tomato" and "TOMATO " are treated as the same ingredient.
- Units are converted, so 1 kg of flour covers a recipe that needs 150 g.
- Duplicate pantry rows for the same ingredient are added together.

The logic is in the `logic` package (`IngredientNormalizer`, `UnitConverter` and `RecipeMatcher`) and is covered by unit tests in `RecipeMatcherTest`.

## Database choice: SQLite

I chose SQLite (using `SQLiteOpenHelper`) because it runs locally on the device, so the app works offline with no server, accounts or internet connection to set up. The pantry and recipes are small, structured data, which suits SQL tables well. The data persists after the app is closed and reopened.

Tables:

- `pantry` (id, name, quantity, unit, expiry_date)
- `recipes` (id, name, steps)
- `recipe_ingredients` (id, recipe_id, name, quantity, unit), where each row links to a recipe through `recipe_id`

## Project structure

- `model`: PantryItem, Recipe and RecipeIngredient
- `db`: DatabaseHelper (table creation, pantry CRUD, recipe queries)
- `util`: SeedData (the 17 recipes) and AppSettings (SharedPreferences)
- `logic`: the strict-matching classes
- `adapter`: PantryAdapter and RecipeAdapter for the RecyclerViews
- `ui`: the Fragments and Activities for each screen

## Setup and run instructions

1. Install Android Studio and open it.
2. Clone the repository with `git clone https://github.com/Riahbaloyi01/Smart_Pantry_Manager.git`, then in Android Studio choose **File, Open** and select the cloned folder.
3. Wait for Gradle to finish syncing (the progress bar at the bottom).
4. Create or choose an emulator in **Device Manager** (any phone running Android 7.0 / API 24 or higher), or connect a physical phone with USB debugging turned on.
5. Click the green **Run** button.
6. To run the unit tests, right-click `RecipeMatcherTest` and choose **Run**.

Requirements: Android Studio, minimum SDK 24 (Android 7.0), written entirely in Java.

## Scope

The app deals only with the user's own pantry and recipes. It does not use Google Maps, any mapping SDK, or location services.

## Author

Nonhlanhla Baloyi