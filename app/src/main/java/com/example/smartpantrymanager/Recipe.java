package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String id;
    private String name;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe () {

    }

    public Recipe(String id, String name, String instructions, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    //getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        if (ingredients == null) {
            return new ArrayList<>();
        }
        return ingredients;
    }

    //setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}
