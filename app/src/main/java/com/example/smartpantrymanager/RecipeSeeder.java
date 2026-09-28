package com.example.smartpantrymanager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecipeSeeder {

    private static final String RECIPES_COLLECTION = "recipes";
    private static final String SEEDED_FIELD = "seededByApp";
    private static final String SEEDED_VALUE = "smart-pantry-v1";

    private RecipeSeeder() {

    }

    public static void seedIfNeeded(FirebaseFirestore firestore) {
        firestore.collection(RECIPES_COLLECTION)
                .whereEqualTo(SEEDED_FIELD, SEEDED_VALUE)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        seedRecipes(firestore);
                    }
                });
    }

    private static void seedRecipes(FirebaseFirestore firestore) {
        List<Recipe> recipes = createRecipes();
        com.google.firebase.firestore.WriteBatch batch =
                firestore.batch();

        for (Recipe recipe : recipes) {
            Map<String, Object> recipeData = new HashMap<>();
            recipeData.put("name", recipe.getName());
            recipeData.put("instructions", recipe.getInstructions());
            recipeData.put("ingredients", recipe.getIngredients());
            recipeData.put(SEEDED_FIELD, SEEDED_VALUE);

            batch.set(
                    firestore.collection(RECIPES_COLLECTION)
                            .document(recipeId(recipe.getName())),
                    recipeData,
                    SetOptions.merge()
            );
        }

        batch.commit();
    }

    private static List<Recipe> createRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(new Recipe(
                "",
                "Tomato Omelette",
                "Beat the eggs, add the tomato, and cook in a pan.",
                Arrays.asList(
                        ingredient("tomato", 2, "pieces"),
                        ingredient("egg", 2, "pieces")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Banana Smoothie",
                "Blend the banana, milk, and honey until smooth.",
                Arrays.asList(
                        ingredient("banana", 2, "pieces"),
                        ingredient("milk", 250, "ml"),
                        ingredient("honey", 1, "tablespoon")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Garlic Pasta",
                "Cook the pasta and stir it with garlic and oil.",
                Arrays.asList(
                        ingredient("pasta", 200, "grams"),
                        ingredient("garlic", 2, "cloves"),
                        ingredient("oil", 1, "tablespoon")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Cheese Toast",
                "Place cheese on bread and toast until golden.",
                Arrays.asList(
                        ingredient("bread", 2, "slices"),
                        ingredient("cheese", 2, "slices")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Chicken Rice",
                "Cook the rice and chicken together with the onion.",
                Arrays.asList(
                        ingredient("chicken", 250, "grams"),
                        ingredient("rice", 200, "grams"),
                        ingredient("onion", 1, "piece")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Tuna Sandwich",
                "Mix the tuna with mayonnaise and serve in bread.",
                Arrays.asList(
                        ingredient("tuna", 1, "can"),
                        ingredient("mayonnaise", 2, "tablespoons"),
                        ingredient("bread", 2, "slices")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Vegetable Stir Fry",
                "Stir fry the vegetables in oil until tender.",
                Arrays.asList(
                        ingredient("carrot", 2, "pieces"),
                        ingredient("pepper", 1, "piece"),
                        ingredient("onion", 1, "piece"),
                        ingredient("oil", 1, "tablespoon")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Potato Salad",
                "Boil the potatoes and mix with mayonnaise and onion.",
                Arrays.asList(
                        ingredient("potato", 3, "pieces"),
                        ingredient("mayonnaise", 2, "tablespoons"),
                        ingredient("onion", 1, "piece")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Pancakes",
                "Mix the ingredients and cook the batter in a pan.",
                Arrays.asList(
                        ingredient("flour", 200, "grams"),
                        ingredient("milk", 250, "ml"),
                        ingredient("egg", 2, "pieces")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Apple Oatmeal",
                "Cook the oats with milk and add sliced apple.",
                Arrays.asList(
                        ingredient("oats", 100, "grams"),
                        ingredient("milk", 250, "ml"),
                        ingredient("apple", 1, "piece")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Bean Wrap",
                "Fill the wrap with beans, tomato, and cheese.",
                Arrays.asList(
                        ingredient("beans", 150, "grams"),
                        ingredient("tomato", 1, "piece"),
                        ingredient("cheese", 1, "slice"),
                        ingredient("wrap", 1, "piece")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Mushroom Toast",
                "Cook the mushrooms and serve them on toasted bread.",
                Arrays.asList(
                        ingredient("mushroom", 150, "grams"),
                        ingredient("bread", 2, "slices"),
                        ingredient("butter", 1, "tablespoon")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Lentil Soup",
                "Boil the lentils with carrot, onion, and water.",
                Arrays.asList(
                        ingredient("lentils", 200, "grams"),
                        ingredient("carrot", 1, "piece"),
                        ingredient("onion", 1, "piece"),
                        ingredient("water", 500, "ml")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Avocado Toast",
                "Mash the avocado and spread it on toasted bread.",
                Arrays.asList(
                        ingredient("avocado", 1, "piece"),
                        ingredient("bread", 2, "slices")
                )
        ));

        recipes.add(new Recipe(
                "",
                "Fruit Salad",
                "Chop all fruit and mix together.",
                Arrays.asList(
                        ingredient("apple", 1, "piece"),
                        ingredient("banana", 1, "piece"),
                        ingredient("orange", 1, "piece")
                )
        ));

        return recipes;
    }

    private static RecipeIngredient ingredient(
            String name,
            double quantity,
            String unit
    ) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static String recipeId(String recipeName) {
        return recipeName
                .toLowerCase()
                .replace(" ", "_");
    }
}