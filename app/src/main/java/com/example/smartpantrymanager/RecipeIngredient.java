package com.example.smartpantrymanager;

public class RecipeIngredient {
    private String name;
    private double requiredQuantity;
    private String unit;

    public RecipeIngredient () {

    }

    public RecipeIngredient(String name, double requiredQuantity, String unit) {
        this.name = name;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    //getters

    public String getName() {
        return name;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public String getUnit() {
        return unit;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
