package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.text.TextUtils;
import android.widget.Toast;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddIngredientActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_MODE =
            "com.example.smartpantrymanager.EXTRA_EDIT_MODE";

    public static final String EXTRA_ITEM_ID =
            "com.example.smartpantrymanager.EXTRA_ITEM_ID";

    public static final String EXTRA_ITEM_NAME =
            "com.example.smartpantrymanager.EXTRA_ITEM_NAME";

    public static final String EXTRA_ITEM_QUANTITY =
            "com.example.smartpantrymanager.EXTRA_ITEM_QUANTITY";

    public static final String EXTRA_ITEM_UNIT =
            "com.example.smartpantrymanager.EXTRA_ITEM_UNIT";

    public static final String EXTRA_ITEM_EXPIRY_DATE =
            "com.example.smartpantrymanager.EXTRA_ITEM_EXPIRY_DATE";

    private EditText ingredientNameInput;
    private boolean editMode;
    private String editingItemId;
    private EditText quantityInput;
    private EditText unitInput;
    private EditText expiryDateInput;
    private  FirebaseFirestore firestore;
    private TextView formTitle;
    private Button saveIngredientButton;

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
        formTitle = findViewById(R.id.textAddIngredientTitle);
        saveIngredientButton = findViewById(R.id.buttonSaveIngredient);

        editMode = getIntent().getBooleanExtra(EXTRA_EDIT_MODE, false);

        if (editMode) {
            editingItemId = getIntent().getStringExtra(EXTRA_ITEM_ID);

            ingredientNameInput.setText(
                    getIntent().getStringExtra(EXTRA_ITEM_NAME)
            );

            double quantity = getIntent().getDoubleExtra(EXTRA_ITEM_QUANTITY, 0.0);
            quantityInput.setText(String.valueOf(quantity));

            unitInput.setText(
                    getIntent().getStringExtra(EXTRA_ITEM_UNIT)
            );

            expiryDateInput.setText(
                    getIntent().getStringExtra(EXTRA_ITEM_EXPIRY_DATE)
            );

            formTitle.setText(R.string.edit_ingredient_title);
            saveIngredientButton.setText(R.string.update_ingredient);
        }


        saveIngredientButton.setOnClickListener(view -> validateForm());
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
            double quantity = Double.parseDouble(quantityText);
            String expiryDate = expiryDateInput.getText().toString().trim();

            if (editMode) {
                updateIngredientInFirestore(
                        ingredientName,
                        quantity,
                        unit,
                        expiryDate
                );
            } else {
                saveIngredientToFirestore(
                        ingredientName,
                        quantity,
                        unit,
                        expiryDate
                );
            }
        }
    }

    private void saveIngredientToFirestore(String ingredientName, double quantity, String unit, String expiryDate) {
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
                .addOnFailureListener(exception -> Toast.makeText(this,
                        R.string.ingredient_save_failed,
                        Toast.LENGTH_SHORT
                ).show());
    }

    private void updateIngredientInFirestore(String ingredientName, double quantity, String unit, String expiryDate) {
        if (editingItemId == null || editingItemId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.ingredient_update_failed,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Map<String, Object> pantryItem = new HashMap<>();
        pantryItem.put("name", ingredientName);
        pantryItem.put("quantity", quantity);
        pantryItem.put("unit", unit);
        pantryItem.put("expiryDate", expiryDate);

        firestore.collection("pantryItems")
                .document(editingItemId)
                .update(pantryItem)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            R.string.ingredient_updated,
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(
                            this,
                            R.string.ingredient_update_failed,
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}