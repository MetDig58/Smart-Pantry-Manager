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
    private RecipeAdapter almostThereRecipeAdapter;

    private RecyclerView recipeRecyclerView;
    private RecyclerView almostThereRecipeRecyclerView;

    private TextView noMatchingRecipesText;
    private TextView almostThereTitleText;
    private TextView almostThereDescriptionText;
    private TextView noAlmostThereRecipesText;

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

        almostThereRecipeRecyclerView = findViewById(
                R.id.almostThereRecipesRecyclerView
        );

        noMatchingRecipesText = findViewById(
                R.id.textNoMatchingRecipes
        );

        almostThereTitleText = findViewById(
                R.id.textAlmostThereTitle
        );

        almostThereDescriptionText = findViewById(
                R.id.textAlmostThereDescription
        );

        noAlmostThereRecipesText = findViewById(
                R.id.textNoAlmostThereRecipes
        );

        recipeAdapter = new RecipeAdapter(
                this::openRecipeDetails
        );

        almostThereRecipeAdapter = new RecipeAdapter(
                this::openRecipeDetails
        );

        recipeRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeRecyclerView.setAdapter(recipeAdapter);

        almostThereRecipeRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        almostThereRecipeRecyclerView.setAdapter(
                almostThereRecipeAdapter
        );

        firestore = FirebaseFirestore.getInstance();

        RecipeSeeder.seedIfNeeded(firestore);

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
        List<Recipe> almostThereRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            int unavailableIngredientCount =
                    countUnavailableIngredients(recipe);

            if (unavailableIngredientCount == 0) {
                matchingRecipes.add(recipe);
            } else if (unavailableIngredientCount == 1) {
                almostThereRecipes.add(recipe);
            }
        }

        recipeAdapter.setRecipes(matchingRecipes);
        almostThereRecipeAdapter.setRecipes(almostThereRecipes);

        updateMatchingRecipesDisplay(matchingRecipes);
        updateAlmostThereDisplay(almostThereRecipes);
    }

    private void updateMatchingRecipesDisplay(
            List<Recipe> matchingRecipes
    ) {
        boolean hasMatches = !matchingRecipes.isEmpty();

        if (hasMatches) {
            recipeRecyclerView.setVisibility(View.VISIBLE);
            noMatchingRecipesText.setVisibility(View.GONE);
        } else {
            recipeRecyclerView.setVisibility(View.GONE);
            noMatchingRecipesText.setVisibility(View.VISIBLE);
        }
    }

    private void updateAlmostThereDisplay(
            List<Recipe> almostThereRecipes
    ) {
        boolean hasAlmostThereRecipes =
                !almostThereRecipes.isEmpty();

        almostThereTitleText.setVisibility(View.VISIBLE);
        almostThereDescriptionText.setVisibility(View.VISIBLE);

        if (hasAlmostThereRecipes) {
            almostThereRecipeRecyclerView.setVisibility(View.VISIBLE);
            noAlmostThereRecipesText.setVisibility(View.GONE);
        } else {
            almostThereRecipeRecyclerView.setVisibility(View.GONE);
            noAlmostThereRecipesText.setVisibility(View.VISIBLE);
        }
    }

    private boolean canMakeRecipe(Recipe recipe) {
        return countUnavailableIngredients(recipe) == 0;
    }

    private int countUnavailableIngredients(Recipe recipe) {
        if (recipe == null || recipe.getIngredients() == null) {
            return 1;
        }

        int unavailableIngredientCount = 0;

        for (RecipeIngredient requiredIngredient :
                recipe.getIngredients()) {

            if (!isIngredientAvailable(requiredIngredient)) {
                unavailableIngredientCount++;
            }
        }

        return unavailableIngredientCount;
    }

    private boolean isIngredientAvailable(
            RecipeIngredient requiredIngredient
    ) {
        if (requiredIngredient == null) {
            return false;
        }

        PantryItem matchingPantryItem =
                findMatchingPantryItem(requiredIngredient.getName());

        if (matchingPantryItem == null) {
            return false;
        }

        String pantryUnit = normaliseUnit(
                matchingPantryItem.getUnit()
        );

        String requiredUnit = normaliseUnit(
                requiredIngredient.getUnit()
        );

        if (!unitCategoriesMatch(pantryUnit, requiredUnit)) {
            return false;
        }

        Double pantryQuantity = convertToBaseQuantity(
                matchingPantryItem.getQuantity(),
                pantryUnit
        );

        Double requiredQuantity = convertToBaseQuantity(
                requiredIngredient.getRequiredQuantity(),
                requiredUnit
        );

        if (pantryQuantity == null || requiredQuantity == null) {
            return false;
        }

        return pantryQuantity >= requiredQuantity;
    }

    private Double convertToBaseQuantity(
            double quantity,
            String normalisedUnit
    ) {
        switch (normalisedUnit) {
            case "piece":
            case "gram":
            case "millilitre":
            case "slice":
            case "can":
            case "clove":
            case "tablespoon":
            case "teaspoon":
            case "cup":
                return quantity;

            case "kilogram":
                return quantity * 1000;

            case "litre":
                return quantity * 1000;

            default:
                return null;
        }
    }

    private boolean unitCategoriesMatch(
            String pantryUnit,
            String requiredUnit
    ) {
        return getUnitCategory(pantryUnit)
                .equals(getUnitCategory(requiredUnit));
    }

    private String getUnitCategory(String unit) {
        switch (unit) {
            case "piece":
                return "count";

            case "gram":
            case "kilogram":
                return "mass";

            case "millilitre":
            case "litre":
                return "volume";

            case "slice":
            case "can":
            case "clove":
            case "tablespoon":
            case "teaspoon":
            case "cup":
                return unit;

            default:
                return "unknown";
        }
    }

    private String normaliseUnit(String unit) {
        if (unit == null) {
            return "";
        }

        String normalisedUnit = unit
                .trim()
                .toLowerCase(Locale.ROOT);

        switch (normalisedUnit) {
            case "piece":
            case "pieces":
            case "item":
            case "items":
                return "piece";

            case "g":
            case "gram":
            case "grams":
                return "gram";

            case "kg":
            case "kilogram":
            case "kilograms":
                return "kilogram";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "millilitre";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "litre";

            case "slice":
            case "slices":
                return "slice";

            case "can":
            case "cans":
                return "can";

            case "clove":
            case "cloves":
                return "clove";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tablespoon";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "teaspoon";

            case "cup":
            case "cups":
                return "cup";

            default:
                return normalisedUnit;
        }
    }

    private PantryItem findMatchingPantryItem(
            String ingredientName
    ) {
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