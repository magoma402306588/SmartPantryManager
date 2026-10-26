package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView rvPantryItems;
    private LinearLayout layoutEmptyState;
    private TextView tvPantrySummary;
    private Button btnCookNow;
    private FloatingActionButton fabAddIngredient;
    private PantryAdapter adapter;
    private final List<PantryItem> pantryItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Safe toolbar initialization
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        databaseHelper = new DatabaseHelper(this);

        rvPantryItems = findViewById(R.id.rvPantryItems);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvPantrySummary = findViewById(R.id.tvPantrySummary);
        btnCookNow = findViewById(R.id.btnCookNow);
        fabAddIngredient = findViewById(R.id.fabAddIngredient);

        if (rvPantryItems != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvPantryItems.setLayoutManager(layoutManager);
            rvPantryItems.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
            adapter = new PantryAdapter(pantryItemList);
            rvPantryItems.setAdapter(adapter);
        }

        if (fabAddIngredient != null) {
            fabAddIngredient.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(intent);
            });
        }

        if (btnCookNow != null) {
            btnCookNow.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryData();
    }

    private void loadPantryData() {
        pantryItemList.clear();
        try {
            Cursor cursor = databaseHelper.getAllPantryItems();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_NAME));
                    double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_QTY));
                    String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_UNIT));
                    String expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_EXPIRY));

                    pantryItemList.add(new PantryItem(id, name, quantity, unit, expiry));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (pantryItemList.isEmpty()) {
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
            if (rvPantryItems != null) rvPantryItems.setVisibility(View.GONE);
            if (tvPantrySummary != null) tvPantrySummary.setText("Pantry (0 items)");
        } else {
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
            if (rvPantryItems != null) rvPantryItems.setVisibility(View.VISIBLE);
            if (tvPantrySummary != null) tvPantrySummary.setText("Pantry (" + pantryItemList.size() + " items)");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "Settings")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {
        private final List<PantryItem> itemList;

        public PantryAdapter(List<PantryItem> itemList) {
            this.itemList = itemList;
        }

        @NonNull
        @Override
        public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new PantryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
            PantryItem item = itemList.get(position);
            String rawName = item.getName();
            String capName = rawName.isEmpty() ? "" : rawName.substring(0, 1).toUpperCase() + rawName.substring(1);
            holder.tvTitle.setText(capName);

            StringBuilder details = new StringBuilder();
            details.append("Available: ").append(item.getQuantity()).append(" ").append(item.getUnit());
            if (item.getExpiryDate() != null && !item.getExpiryDate().trim().isEmpty()) {
                details.append(" | Expires: ").append(item.getExpiryDate());
            }
            holder.tvSubtitle.setText(details.toString());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra("item_id", item.getId());
                intent.putExtra("item_name", item.getName());
                intent.putExtra("item_qty", item.getQuantity());
                intent.putExtra("item_unit", item.getUnit());
                intent.putExtra("item_expiry", item.getExpiryDate());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        class PantryViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle;
            public PantryViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(android.R.id.text1);
                tvSubtitle = itemView.findViewById(android.R.id.text2);
                tvTitle.setTextSize(16f);
                tvSubtitle.setTextColor(0xFF666666);
            }
        }
    }
}