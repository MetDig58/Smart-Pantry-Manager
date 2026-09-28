package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.StringJoiner;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID =
            "com.example.smartpantrymanager.EXTRA_RECIPE_ID";

    private FirebaseFirestore firestore;

    private TextView recipeNameText;
    private TextView recipeIngredientsText;
    private TextView recipeInstructionsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

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

        recipeNameText = findViewById(R.id.textRecipeDetailName);
        recipeIngredientsText =
                findViewById(R.id.textRecipeDetailIngredients);
        recipeInstructionsText =
                findViewById(R.id.textRecipeDetailInstructions);

        firestore = FirebaseFirestore.getInstance();

        String recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);

        if (recipeId == null || recipeId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.recipe_detail_missing_id,
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        loadRecipe(recipeId);
    }

    private void loadRecipe(String recipeId) {
        firestore.collection("recipes")
                .document(recipeId)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        showLoadErrorAndClose();
                        return;
                    }

                    Recipe recipe = document.toObject(Recipe.class);

                    if (recipe == null) {
                        showLoadErrorAndClose();
                        return;
                    }

                    displayRecipe(recipe);
                })
                .addOnFailureListener(exception -> showLoadErrorAndClose());
    }

    private void displayRecipe(Recipe recipe) {
        recipeNameText.setText(recipe.getName());
        recipeIngredientsText.setText(
                buildIngredientText(recipe)
        );
        recipeInstructionsText.setText(
                recipe.getInstructions()
        );
    }

    private String buildIngredientText(Recipe recipe) {
        StringJoiner ingredientText = new StringJoiner("\n");

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            String line = String.format(
                    Locale.getDefault(),
                    "• %s - %s %s",
                    ingredient.getName(),
                    ingredient.getRequiredQuantity(),
                    ingredient.getUnit()
            );

            ingredientText.add(line);
        }

        return ingredientText.toString();
    }

    private void showLoadErrorAndClose() {
        Toast.makeText(
                this,
                R.string.recipe_detail_load_failed,
                Toast.LENGTH_LONG
        ).show();

        finish();
    }
}