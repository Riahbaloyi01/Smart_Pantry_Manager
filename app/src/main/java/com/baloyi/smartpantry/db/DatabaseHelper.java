package com.baloyi.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.baloyi.smartpantry.model.Recipe;
import com.baloyi.smartpantry.model.RecipeIngredient;
import com.baloyi.smartpantry.util.SeedData;

import com.baloyi.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pantry.db";
    private static final int DB_VERSION = 1;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry";
    // Recipe tables
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Runs once, the first time the database is created on the phone
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "quantity REAL NOT NULL, "
                + "unit TEXT NOT NULL, "
                + "expiry_date TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, "
                + "name TEXT NOT NULL, "
                + "quantity REAL NOT NULL, "
                + "unit TEXT NOT NULL, "
                + "FOREIGN KEY(recipe_id) REFERENCES " + TABLE_RECIPES + "(id))");

        SeedData.seed(db);
    }

    // Runs when DB_VERSION goes up; simple approach: rebuild the tables
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // CREATE: add a new pantry item, returns the new row id
    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);
        return db.insert(TABLE_PANTRY, null, values);
    }

    // READ: get every pantry item, ordered by name
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, "name COLLATE NOCASE ASC");
        while (cursor.moveToNext()) {
            items.add(new PantryItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                    cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))));
        }
        cursor.close();
        return items;
    }

    // UPDATE: change an existing item, returns how many rows changed
    public int updatePantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);
        return db.update(TABLE_PANTRY, values, "id = ?", new String[]{String.valueOf(id)});
    }

    // DELETE: remove an item by id
    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, "id = ?", new String[]{String.valueOf(id)});
    }

    // READ: get every recipe together with its ingredients
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor rc = db.query(TABLE_RECIPES, null, null, null, null, null, "name ASC");
        while (rc.moveToNext()) {
            long id = rc.getLong(rc.getColumnIndexOrThrow("id"));
            Recipe recipe = new Recipe(id,
                    rc.getString(rc.getColumnIndexOrThrow("name")),
                    rc.getString(rc.getColumnIndexOrThrow("steps")));

            Cursor ic = db.query(TABLE_RECIPE_INGREDIENTS, null, "recipe_id = ?",
                    new String[]{String.valueOf(id)}, null, null, null);
            while (ic.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(
                        ic.getString(ic.getColumnIndexOrThrow("name")),
                        ic.getDouble(ic.getColumnIndexOrThrow("quantity")),
                        ic.getString(ic.getColumnIndexOrThrow("unit"))));
            }
            ic.close();
            recipes.add(recipe);
        }
        rc.close();
        return recipes;
    }

    // READ: get one recipe (with its ingredients) by id
    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Recipe recipe = null;

        Cursor rc = db.query(TABLE_RECIPES, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (rc.moveToFirst()) {
            recipe = new Recipe(id,
                    rc.getString(rc.getColumnIndexOrThrow("name")),
                    rc.getString(rc.getColumnIndexOrThrow("steps")));

            Cursor ic = db.query(TABLE_RECIPE_INGREDIENTS, null, "recipe_id = ?",
                    new String[]{String.valueOf(id)}, null, null, null);
            while (ic.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(
                        ic.getString(ic.getColumnIndexOrThrow("name")),
                        ic.getDouble(ic.getColumnIndexOrThrow("quantity")),
                        ic.getString(ic.getColumnIndexOrThrow("unit"))));
            }
            ic.close();
        }
        rc.close();
        return recipe;
    }

}