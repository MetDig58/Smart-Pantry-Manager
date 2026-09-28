package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String SETTINGS_FILE = "smart_pantry_settings";
    private static final String EXPIRY_ALERTS_KEY = "expiry_alerts_enabled";

    private Switch expiryAlertsSwitch;
    private SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        expiryAlertsSwitch = findViewById(R.id.switchExpiryAlerts);

        settingsPreferences = getSharedPreferences(
                SETTINGS_FILE,
                MODE_PRIVATE
        );

        boolean expiryAlertsEnabled = settingsPreferences.getBoolean(
                EXPIRY_ALERTS_KEY,
                false
        );

        expiryAlertsSwitch.setChecked(expiryAlertsEnabled);

        expiryAlertsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    settingsPreferences.edit()
                            .putBoolean(EXPIRY_ALERTS_KEY, isChecked)
                            .apply();
                }
        );
    }
}