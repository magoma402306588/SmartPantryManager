package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INGREDIENTS = "ingredients";
    public static final String COL_RECIPE_STEPS = "steps";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantry = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT, " +
                COL_PANTRY_EXPIRY + " TEXT);";
        db.execSQL(createPantry);

        String createRecipes = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_INGREDIENTS + " TEXT NOT NULL, " +
                COL_RECIPE_STEPS + " TEXT NOT NULL);";
        db.execSQL(createRecipes);

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    public long insertPantryItem(String name, double quantity, String unit, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COL_PANTRY_QTY, quantity);
        cv.put(COL_PANTRY_UNIT, unit.trim().toLowerCase());
        cv.put(COL_PANTRY_EXPIRY, expiry);
        return db.insert(TABLE_PANTRY, null, cv);
    }

    public Cursor getAllPantryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COL_PANTRY_NAME + " ASC", null);
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COL_PANTRY_QTY, quantity);
        cv.put(COL_PANTRY_UNIT, unit.trim().toLowerCase());
        cv.put(COL_PANTRY_EXPIRY, expiry);
        return db.update(TABLE_PANTRY, cv, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
    }

    private void seedRecipes(SQLiteDatabase db) {
        addSeed(db, "Scrambled Eggs", "egg:2:pcs;butter:10:g;salt:1:pinch", "1. Whisk eggs and salt.\n2. Melt butter in pan.\n3. Cook eggs gently until soft curd forms.");
        addSeed(db, "Boiled Eggs", "egg:2:pcs;water:500:ml", "1. Boil water in a pot.\n2. Add eggs gently.\n3. Cook for 8 minutes, cool in water, and peel.");
        addSeed(db, "Tomato Toast", "bread:2:slices;tomato:1:pcs;olive oil:5:ml;salt:1:pinch", "1. Toast bread slices.\n2. Slice fresh tomato on top.\n3. Drizzle oil and season with salt.");
        addSeed(db, "Garlic Butter Pasta", "pasta:150:g;garlic:2:cloves;butter:20:g;salt:1:pinch", "1. Boil pasta in salted water.\n2. Sauté garlic in butter.\n3. Toss pasta in garlic butter.");
        addSeed(db, "Tomato Pasta", "pasta:150:g;tomato:2:pcs;garlic:1:cloves;olive oil:10:ml;salt:1:pinch", "1. Boil pasta.\n2. Sauté garlic and diced tomatoes in olive oil.\n3. Mix pasta into sauce.");
        addSeed(db, "French Toast", "bread:2:slices;egg:1:pcs;milk:50:ml;butter:10:g", "1. Whisk egg and milk.\n2. Dip bread in mixture.\n3. Fry in butter until golden brown.");
        addSeed(db, "Classic Cheese Omelette", "egg:3:pcs;cheese:30:g;butter:10:g;salt:1:pinch", "1. Whisk eggs with salt.\n2. Pour into buttered pan.\n3. Top with cheese and fold.");
        addSeed(db, "Grilled Cheese Sandwich", "bread:2:slices;cheese:50:g;butter:10:g", "1. Butter outer sides of bread.\n2. Put cheese between slices.\n3. Pan-fry until golden and melted.");
        addSeed(db, "Fried Rice", "rice:200:g;egg:1:pcs;onion:1:pcs;oil:10:ml;soy sauce:15:ml", "1. Sauté diced onion in oil.\n2. Scramble egg in pan.\n3. Add rice and soy sauce; toss well.");
        addSeed(db, "Basic Pancakes", "flour:100:g;milk:150:ml;egg:1:pcs;butter:10:g;sugar:20:g", "1. Whisk flour, milk, egg, and sugar.\n2. Melt butter on pan.\n3. Cook batter until golden on both sides.");
        addSeed(db, "Crispy Potato Hash", "potato:2:pcs;onion:1:pcs;oil:15:ml;salt:1:pinch", "1. Dice potatoes and onions.\n2. Fry in oil over medium heat until crispy.\n3. Season with salt.");
        addSeed(db, "Tomato Soup", "tomato:4:pcs;onion:1:pcs;garlic:2:cloves;olive oil:15:ml;salt:1:pinch", "1. Sauté onion and garlic in olive oil.\n2. Simmer diced tomatoes for 15 mins.\n3. Blend until smooth.");
        addSeed(db, "Banana Milkshake", "banana:2:pcs;milk:200:ml;honey:10:g", "1. Peel bananas.\n2. Blend banana, milk, and honey until smooth.\n3. Serve cold.");
        addSeed(db, "Mashed Potatoes", "potato:3:pcs;milk:50:ml;butter:20:g;salt:1:pinch", "1. Boil peeled potatoes until tender.\n2. Drain water completely.\n3. Mash with butter, warm milk, and salt.");
        addSeed(db, "Simple Tuna Salad", "tuna:1:cans;mayonnaise:30:g;onion:1:pcs;salt:1:pinch", "1. Drain canned tuna.\n2. Finely chop onion.\n3. Mix tuna, onion, mayonnaise, and salt.");
        addSeed(db, "Oatmeal Porridge", "oats:50:g;milk:200:ml;honey:15:g", "1. Combine oats and milk in pot.\n2. Simmer for 5 minutes stirring constantly.\n3. Serve with honey.");
    }

    private void addSeed(SQLiteDatabase db, String name, String ingredients, String steps) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_NAME, name);
        cv.put(COL_RECIPE_INGREDIENTS, ingredients);
        cv.put(COL_RECIPE_STEPS, steps);
        db.insert(TABLE_RECIPES, null, cv);
    }
}