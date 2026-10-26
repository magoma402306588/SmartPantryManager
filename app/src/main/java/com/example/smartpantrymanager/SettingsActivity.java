package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Building layout programmatically - eliminates any missing R.layout error!
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);
        layout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Settings & Preferences");
        title.setTextSize(22f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#212121"));
        title.setPadding(0, 0, 0, 32);
        layout.addView(title);

        Switch switchExpiry = new Switch(this);
        switchExpiry.setText("Alert for expiring ingredients");
        switchExpiry.setTextSize(16f);
        layout.addView(switchExpiry);

        TextView desc = new TextView(this);
        desc.setText("Sends reminders when tracked ingredients in your pantry near their expiry dates.");
        desc.setTextColor(Color.parseColor("#757575"));
        desc.setPadding(0, 16, 0, 0);
        layout.addView(desc);

        setContentView(layout);

        SharedPreferences prefs = getSharedPreferences("SmartPantryPrefs", MODE_PRIVATE);
        boolean isAlertEnabled = prefs.getBoolean("expiry_alerts", true);
        switchExpiry.setChecked(isAlertEnabled);

        switchExpiry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("expiry_alerts", isChecked).apply();
            Toast.makeText(SettingsActivity.this, "Preference saved", Toast.LENGTH_SHORT).show();
        });
    }
}