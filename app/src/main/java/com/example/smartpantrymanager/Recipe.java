package com.example.smartpantrymanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {
    private long id;
    private String name;
    private String ingredientsRaw;
    private String steps;

    public Recipe(long id, String name, String ingredientsRaw, String steps) {
        this.id = id;
        this.name = name;
        this.ingredientsRaw = ingredientsRaw;
        this.steps = steps;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getIngredientsRaw() { return ingredientsRaw; }
    public String getSteps() { return steps; }

    public List<RecipeIngredient> getIngredientList() {
        List<RecipeIngredient> list = new ArrayList<>();
        if (ingredientsRaw == null || ingredientsRaw.isEmpty()) return list;

        String[] items = ingredientsRaw.split(";");
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length >= 3) {
                try {
                    double qty = Double.parseDouble(parts[1]);
                    list.add(new RecipeIngredient(parts[0], qty, parts[2]));
                } catch (NumberFormatException ignored) {}
            }
        }
        return list;
    }
}
