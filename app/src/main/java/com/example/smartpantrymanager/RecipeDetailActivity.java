package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvTitle = findViewById(R.id.tvDetailRecipeTitle);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvSteps = findViewById(R.id.tvDetailSteps);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe_obj");

        if (recipe != null) {
            tvTitle.setText(recipe.getName());

            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ri : recipe.getIngredientList()) {
                sb.append("• ").append(ri.toString()).append("\n");
            }
            tvIngredients.setText(sb.toString().trim());
            tvSteps.setText(recipe.getSteps());
        }
    }
}