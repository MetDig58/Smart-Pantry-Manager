package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ListenerRegistration pantryListener;
    private ListenerRegistration recipeListener;

    private final List<PantryItem> pantryItems = new ArrayList<>();
    private final List<Recipe> recipes = new ArrayList<>();

    private RecipeAdapter recipeAdapter;
    private RecyclerView recipeRecyclerView;
    private TextView noMatchingRecipesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

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

        recipeRecyclerView = findViewById(
                R.id.suggestedRecipesRecyclerView
        );
        noMatchingRecipesText = findViewById(
                R.id.textNoMatchingRecipes
        );

        recipeAdapter = new RecipeAdapter(
                this::openRecipeDetails
        );

        recipeRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );
        recipeRecyclerView.setAdapter(recipeAdapter);

        firestore = FirebaseFirestore.getInstance();

        listenForPantryItems();
        listenForRecipes();
    }

    private void listenForPantryItems() {
        pantryListener = firestore.collection("pantryItems")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        showLoadError();
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    pantryItems.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : snapshot.getDocuments()) {

                        PantryItem pantryItem =
                                document.toObject(PantryItem.class);

                        if (pantryItem != null) {
                            pantryItem.setId(document.getId());
                            pantryItems.add(pantryItem);
                        }
                    }

                    updateSuggestions();
                });
    }

    private void listenForRecipes() {
        recipeListener = firestore.collection("recipes")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        showLoadError();
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    recipes.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : snapshot.getDocuments()) {

                        Recipe recipe = document.toObject(Recipe.class);

                        if (recipe != null) {
                            recipe.setId(document.getId());
                            recipes.add(recipe);
                        }
                    }

                    updateSuggestions();
                });
    }

    private void updateSuggestions() {
        List<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (canMakeRecipe(recipe)) {
                matchingRecipes.add(recipe);
            }
        }

        recipeAdapter.setRecipes(matchingRecipes);

        boolean hasMatches = !matchingRecipes.isEmpty();

        if (hasMatches) {
            recipeRecyclerView.setVisibility(View.VISIBLE);
            noMatchingRecipesText.setVisibility(View.GONE);
        } else {
            recipeRecyclerView.setVisibility(View.GONE);
            noMatchingRecipesText.setVisibility(View.VISIBLE);
        }
    }

    private boolean canMakeRecipe(Recipe recipe) {
        for (RecipeIngredient requiredIngredient : recipe.getIngredients()) {
            PantryItem matchingPantryItem =
                    findMatchingPantryItem(requiredIngredient.getName());

            if (matchingPantryItem == null) {
                return false;
            }

            if (matchingPantryItem.getQuantity()
                    < requiredIngredient.getRequiredQuantity()) {
                return false;
            }
        }

        return true;
    }

    private PantryItem findMatchingPantryItem(String ingredientName) {
        String normalisedRecipeName =
                normaliseIngredientName(ingredientName);

        for (PantryItem pantryItem : pantryItems) {
            String normalisedPantryName =
                    normaliseIngredientName(pantryItem.getName());

            if (normalisedRecipeName.equals(normalisedPantryName)) {
                return pantryItem;
            }
        }

        return null;
    }

    private String normaliseIngredientName(String ingredientName) {
        if (ingredientName == null) {
            return "";
        }

        String normalisedName = ingredientName
                .trim()
                .toLowerCase(Locale.ROOT);

        if (normalisedName.endsWith("ies")) {
            return normalisedName.substring(
                    0,
                    normalisedName.length() - 3
            ) + "y";
        }

        if (normalisedName.endsWith("s")
                && !normalisedName.endsWith("ss")) {
            return normalisedName.substring(
                    0,
                    normalisedName.length() - 1
            );
        }

        return normalisedName;
    }

    private void openRecipeDetails(Recipe recipe) {
        Intent intent = new Intent(
                SuggestedRecipesActivity.this,
                RecipeDetailActivity.class
        );

        intent.putExtra(
                RecipeDetailActivity.EXTRA_RECIPE_ID,
                recipe.getId()
        );

        startActivity(intent);
    }

    private void showLoadError() {
        Toast.makeText(
                this,
                R.string.recipe_load_failed,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryListener != null) {
            pantryListener.remove();
        }

        if (recipeListener != null) {
            recipeListener.remove();
        }
    }
}