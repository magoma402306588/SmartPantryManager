package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave, btnDelete, btnCancelBack;
    private TextView tvTitle;
    private DatabaseHelper dbHelper;
    private long itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancelBack = findViewById(R.id.btnCancelBack);

        // Connect the Back button so clicking it returns to previous screen
        if (btnCancelBack != null) {
            btnCancelBack.setOnClickListener(v -> finish());
        }

        // Check if editing an existing item
        if (getIntent().hasExtra("item_id")) {
            itemId = getIntent().getLongExtra("item_id", -1);
            if (tvTitle != null) tvTitle.setText("Edit Ingredient");
            etName.setText(getIntent().getStringExtra("item_name"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("item_qty", 1.0)));
            etUnit.setText(getIntent().getStringExtra("item_unit"));
            etExpiry.setText(getIntent().getStringExtra("item_expiry"));
            if (btnDelete != null) btnDelete.setVisibility(View.VISIBLE);
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveItem());
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> deleteItem());
        }
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingredient name is required");
            return;
        }

        if (TextUtils.isEmpty(qtyStr)) {
            etQuantity.setError("Quantity is required");
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                etQuantity.setError("Quantity must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            return;
        }

        if (TextUtils.isEmpty(unit)) {
            unit = "pcs";
        }

        if (itemId == -1) {
            dbHelper.insertPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, "Item added to pantry", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void deleteItem() {
        if (itemId != -1) {
            dbHelper.deletePantryItem(itemId);
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}