package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    public  interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener recipeClickListener;

    public RecipeAdapter(OnRecipeClickListener recipeClickListener) {
        this.recipeClickListener = recipeClickListener;
    }

    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();

        if (newRecipes != null) {
            recipes.addAll(newRecipes);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recipe, parent, false);

            return new RecipeViewHolder(view, recipeClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout recipeRowContainer;
        private final TextView recipeNameText;
        private final TextView recipeIngredientsText;
        private final OnRecipeClickListener recipeClickListener;

        RecipeViewHolder(@NonNull View itemView, OnRecipeClickListener recipeClickListener) {
            super(itemView);

            this.recipeClickListener = recipeClickListener;

            recipeRowContainer = itemView.findViewById(R.id.recipeRowContainer);
            recipeNameText = itemView.findViewById(R.id.textRecipeName);
            recipeIngredientsText = itemView.findViewById(R.id.textRecipeIngredients);
        }

        void bind (Recipe recipe) {
            recipeNameText.setText(recipe.getName());

            StringJoiner ingredientNames = new StringJoiner(", ");

            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredientNames.add(ingredient.getName());
            }

            recipeIngredientsText.setText(itemView.getContext().getString(R.string.recipe_ingredients_format, ingredientNames.toString()));

            recipeRowContainer.setOnClickListener(view -> recipeClickListener.onRecipeClick(recipe));
        }
    }
}
