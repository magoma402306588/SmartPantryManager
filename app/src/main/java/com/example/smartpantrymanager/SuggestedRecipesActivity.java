package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvSuggestions;
    private TextView tvEmptySuggestions;
    private final List<Recipe> suggestedRecipes = new ArrayList<>();
    private SuggestedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root container
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 24, 32, 32);
        root.setBackgroundColor(Color.WHITE);

        // --- TOP NAVIGATION BAR WITH VISIBLE BACK BUTTON ---
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(0, 0, 0, 24);

        Button btnBack = new Button(this);
        btnBack.setText("← Back");
        btnBack.setTextSize(14f);
        btnBack.setAllCaps(false);
        btnBack.setOnClickListener(v -> finish()); // Closes this screen and goes back

        TextView header = new TextView(this);
        header.setText("Suggested Recipes");
        header.setTextSize(20f);
        header.setTypeface(null, Typeface.BOLD);
        header.setTextColor(Color.parseColor("#212121"));
        header.setPadding(24, 0, 0, 0);

        topBar.addView(btnBack);
        topBar.addView(header);
        root.addView(topBar);
        // ----------------------------------------------------

        tvEmptySuggestions = new TextView(this);
        tvEmptySuggestions.setText("No recipes match your pantry yet.\n\nAdd more ingredients to find recipes you can make right now!");
        tvEmptySuggestions.setTextSize(16f);
        tvEmptySuggestions.setTextColor(Color.parseColor("#757575"));
        tvEmptySuggestions.setPadding(16, 64, 16, 0);
        tvEmptySuggestions.setVisibility(View.GONE);
        root.addView(tvEmptySuggestions);

        rvSuggestions = new RecyclerView(this);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuggestedAdapter(suggestedRecipes);
        rvSuggestions.setAdapter(adapter);
        root.addView(rvSuggestions, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(root);

        dbHelper = new DatabaseHelper(this);
        runStrictMatching();
    }

    private void runStrictMatching() {
        Map<String, Double> pantryMap = new HashMap<>();
        Cursor pCursor = dbHelper.getAllPantryItems();
        if (pCursor != null && pCursor.moveToFirst()) {
            do {
                String name = pCursor.getString(pCursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_NAME));
                double qty = pCursor.getDouble(pCursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_QTY));
                String normName = RecipeMatcher.normalize(name);
                pantryMap.put(normName, pantryMap.getOrDefault(normName, 0.0) + qty);
            } while (pCursor.moveToNext());
            pCursor.close();
        }

        suggestedRecipes.clear();
        Cursor rCursor = dbHelper.getAllRecipes();
        if (rCursor != null && rCursor.moveToFirst()) {
            do {
                long id = rCursor.getLong(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_ID));
                String name = rCursor.getString(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_NAME));
                String ingredients = rCursor.getString(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_INGREDIENTS));
                String steps = rCursor.getString(rCursor.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_STEPS));

                if (RecipeMatcher.isMatch(ingredients, pantryMap)) {
                    suggestedRecipes.add(new Recipe(id, name, ingredients, steps));
                }
            } while (rCursor.moveToNext());
            rCursor.close();
        }

        adapter.notifyDataSetChanged();

        if (suggestedRecipes.isEmpty()) {
            tvEmptySuggestions.setVisibility(View.VISIBLE);
            rvSuggestions.setVisibility(View.GONE);
        } else {
            tvEmptySuggestions.setVisibility(View.GONE);
            rvSuggestions.setVisibility(View.VISIBLE);
        }
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvSummary;

        public RecipeViewHolder(@NonNull View itemView, TextView tvName, TextView tvSummary) {
            super(itemView);
            this.tvName = tvName;
            this.tvSummary = tvSummary;
        }
    }

    private class SuggestedAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
        private final List<Recipe> list;

        public SuggestedAdapter(List<Recipe> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout itemLayout = new LinearLayout(parent.getContext());
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(32, 28, 32, 28);
            itemLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 16);
            itemLayout.setLayoutParams(lp);

            TextView tvName = new TextView(parent.getContext());
            tvName.setTextSize(18f);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(Color.parseColor("#212121"));

            TextView tvSummary = new TextView(parent.getContext());
            tvSummary.setTextSize(14f);
            tvSummary.setTextColor(Color.parseColor("#666666"));
            tvSummary.setPadding(0, 8, 0, 0);

            itemLayout.addView(tvName);
            itemLayout.addView(tvSummary);

            return new RecipeViewHolder(itemLayout, tvName, tvSummary);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
            Recipe r = list.get(position);
            holder.tvName.setText(r.getName());
            holder.tvSummary.setText(r.getIngredientList().size() + " ingredients (All in stock)");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipe_obj", r);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}