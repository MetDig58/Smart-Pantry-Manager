package com.example.smartpantrymanager;

import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddIngredientActivity extends AppCompatActivity {
    private EditText ingredientNameInput;
    private EditText quantityInput;
    private EditText unitInput;
    private EditText expiryDateInput;
    private  FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_ingredient);
        firestore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ingredientNameInput = findViewById(R.id.editTextIngredientName);
        quantityInput = findViewById(R.id.editTextQuantity);
        unitInput = findViewById(R.id.editTextUnit);
        expiryDateInput = findViewById(R.id.editTextExpiryDate);

        Button saveIngredientsButton = findViewById(R.id.buttonSaveIngredient);

        saveIngredientsButton.setOnClickListener(view -> validateForm());
    }

    private void validateForm() {
        String ingredientName = ingredientNameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String unit = unitInput.getText().toString().trim();

        boolean isValid = true;

        ingredientNameInput.setError(null);
        quantityInput.setError(null);
        unitInput.setError(null);

        if (TextUtils.isEmpty(ingredientName)) {
        ingredientNameInput.setError(getString(R.string.ingredient_name_required));
        isValid = false;
        }

        if (TextUtils.isEmpty(quantityText)) {
            quantityInput.setError(getString(R.string.quantity_required));
            isValid = false;
        } else {
            try {
                double quantity = Double.parseDouble(quantityText);

                if (quantity <= 0) {
                    quantityInput.setError(getString(R.string.quantity_invalid));
                    isValid = false;
                }
            } catch (NumberFormatException exception) {
                quantityInput.setError(getString(R.string.quantity_invalid));
            }
        }

        if (TextUtils.isEmpty(unit)) {
            unitInput.setError(getString(R.string.unit_required));
            isValid = false;
        }

        if (isValid) {
            saveIngredientToFirestore(
                    ingredientName,
                    quantityText,
                    unit,
                    expiryDateInput.getText().toString().trim()
            );
        }
    }

    private void saveIngredientToFirestore(String ingredientName, String quantityText, String unit, String expiryDate) {
        double quantity = Double.parseDouble(quantityText);

        Map<String, Object> pantryItem = new HashMap<>();
        pantryItem.put("name", ingredientName);
        pantryItem.put("quantity", quantity);
        pantryItem.put("unit", unit);
        pantryItem.put("expiryDate", expiryDate);

        firestore.collection("pantryItems")
                .add(pantryItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(
                            this,
                            R.string.ingredient_saved,
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
        })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this,
                            R.string.ingredient_save_failed,
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}