# Smart Pantry Manager

Smart Pantry Manager is a Java Android application developed for the Mobile App Development 700 practical assignment. It helps reduce household food waste by suggesting recipes based strictly on ingredients already available in the user's pantry.

The application does not suggest recipes that require missing ingredients or quantities that the user does not have.

## Features

- Add pantry ingredients.
- View pantry ingredients in a RecyclerView.
- Edit existing pantry ingredients.
- Delete pantry ingredients.
- Store pantry data in Firebase Cloud Firestore.
- Persist pantry data after the application is closed and reopened.
- Seed 15 recipes automatically on first use.
- Display only recipes that strictly match the pantry contents.
- Check whether the available quantity is sufficient.
- Support basic singular and plural ingredient matching.
- Support compatible measurement-unit comparisons.
- Display recipe ingredients, quantities, units, and instructions.
- Display a message when no recipes match.
- Open a recipe detail screen from the suggested recipes list.
- Save the expiry-alert preference locally.
- Use Android Activities and Intents for navigation.
- Validate pantry form input.

## Technology Used

- Java
- Android Studio
- XML layouts
- Firebase Cloud Firestore
- AndroidX RecyclerView
- Android Activities
- Android Intents
- SharedPreferences
- Gradle Kotlin DSL

## Application Screens

The application contains the following screens:

1. Pantry List
    - Displays pantry ingredients stored in Firestore.
    - Provides access to the Add Ingredient, Suggested Recipes, and Settings screens.

2. Add Ingredient
    - Allows the user to create a pantry item.
    - Validates the ingredient name, quantity, and unit.

3. Edit Ingredient
    - Reuses the Add Ingredient form.
    - Pre-fills the selected pantry item.
    - Updates the existing Firestore document.

4. Suggested Recipes
    - Displays only recipes that can currently be prepared from the pantry.
    - Shows a message when no recipes match.

5. Recipe Details
    - Displays the selected recipe name, ingredients, quantities, units, and instructions.

6. Settings
    - Contains the expiry-alert preference.
    - Stores the preference using Android SharedPreferences.

## Firestore Database Structure

### pantryItems

Pantry items are stored in the pantryItems collection.

Each pantry document contains:

- name: String
- quantity: Number
- unit: String
- expiryDate: String

Example pantry document:

pantryItems/{documentId}

- name: Tomato
- quantity: 5
- unit: pieces
- expiryDate: empty string

### recipes

Recipes are stored in the recipes collection.

Each recipe document contains:

- name: String
- instructions: String
- ingredients: Array
- seededByApp: String

Each ingredient in the ingredients array contains:

- name: String
- requiredQuantity: Number
- unit: String

Example recipe:

recipes/tomato_omelette

- name: Tomato Omelette
- instructions: Beat the eggs, add the tomato, and cook in a pan.
- seededByApp: smart-pantry-v1

Ingredients:

- name: tomato
- requiredQuantity: 2
- unit: pieces

- name: egg
- requiredQuantity: 2
- unit: pieces

## Strict Recipe-Matching Rule

The strict matching rule is the core business logic of the application.

A recipe is displayed only when:

1. Every required ingredient exists in the pantry.
2. The available quantity is equal to or greater than the required quantity.
3. Ingredient names match after basic normalisation.
4. Pantry and recipe units are compatible.

If one required ingredient is missing, the recipe is excluded.

If one required quantity is insufficient, the recipe is excluded.

### Ingredient normalisation

The application handles simple differences such as:

- Tomato and tomato are treated as the same ingredient.
- Tomatoes and tomato are treated as the same ingredient.
- Piece and pieces are treated as the same unit.

### Unit handling

The application supports compatible units such as:

- g, gram, and grams
- kg, kilogram, and kilograms
- ml, millilitre, and millilitres
- l, litre, and litres
- piece, pieces, item, and items

It also converts compatible measurements:

- 1 kilogram equals 1000 grams.
- 1 litre equals 1000 millilitres.

Incompatible units are rejected. For example:

- 2 pieces does not match 2 grams.
- 2 slices does not match 2 cans.

This prevents invalid recipes from appearing as suggestions.

## Firebase Recipe Seeding

The application includes a RecipeSeeder utility class.

When the Suggested Recipes screen is opened, the application checks whether seeded recipes already exist. If no recipes with the current seed version are found, the application creates the initial recipe collection in Firestore.

The seeder creates at least 15 recipes:

- Tomato Omelette
- Banana Smoothie
- Garlic Pasta
- Cheese Toast
- Chicken Rice
- Tuna Sandwich
- Vegetable Stir Fry
- Potato Salad
- Pancakes
- Apple Oatmeal
- Bean Wrap
- Mushroom Toast
- Lentil Soup
- Avocado Toast
- Fruit Salad

The seeder uses fixed document IDs based on recipe names so that the same recipes are not repeatedly added when the application is reopened.

## Firebase Setup

To set up the project:

1. Install Android Studio.
2. Clone this repository.
3. Open the project in Android Studio.
4. Create or select a Firebase project.
5. Register an Android application using the package name:

com.example.smartpantrymanager

6. Download the Firebase Android configuration file.
7. Place the configuration file in:

app/google-services.json

8. Open the Firebase Console.
9. Enable Cloud Firestore.
10. Configure appropriate Firestore security rules.
11. Allow Gradle to synchronise.
12. Build the project.
13. Connect an Android phone with USB debugging enabled.
14. Run the application.

Do not add Firebase service-account private keys or other secret credentials to the repository.

## Running the Application

After launching the application:

1. Open the Pantry screen.
2. Tap Add Ingredient.
3. Enter an ingredient name, quantity, and unit.
4. Optionally enter an expiry date.
5. Tap Save Ingredient.
6. Confirm that the item appears in the pantry list.
7. Use Edit to change an existing pantry item.
8. Use Delete to remove a pantry item.
9. Open Suggested Recipes.
10. Select a matching recipe to view its details.
11. Open Settings to enable or disable expiry alerts.

## Validation Rules

The Add/Edit Ingredient form validates that:

- The ingredient name is not empty.
- The quantity is not empty.
- The quantity is greater than zero.
- The unit is not empty.
- The expiry date may be left empty.

Invalid values display an error beside the relevant field and are not saved to Firestore.

## Firebase CRUD Operations

The pantry feature demonstrates complete CRUD functionality:

- Create: Add a new pantry ingredient.
- Read: Display pantry ingredients from Firestore.
- Update: Edit an existing pantry ingredient.
- Delete: Delete a pantry ingredient.
- Persistence: Data remains available after reopening the application.

The Pantry screen uses a Firestore snapshot listener, so the RecyclerView updates when pantry data changes.

## Project Structure

Important Java files:

- AddIngredientActivity.java
- MainActivity.java
- PantryAdapter.java
- PantryItem.java
- Recipe.java
- RecipeAdapter.java
- RecipeDetailActivity.java
- RecipeIngredient.java
- RecipeSeeder.java
- SettingsActivity.java
- SuggestedRecipesActivity.java

Important layout files:

- activity_add_ingredient.xml
- activity_main.xml
- activity_recipe_detail.xml
- activity_settings.xml
- activity_suggested_recipes.xml
- item_pantry.xml
- item_recipe.xml

## Database Choice

Firebase Cloud Firestore was selected because:

- It provides cloud-based data persistence.
- It integrates directly with Android applications.
- It supports real-time snapshot listeners.
- It stores pantry and recipe data in structured collections.
- It avoids the need to create a separate backend server.
- It supports the required Create, Read, Update, and Delete operations.

## Testing Performed

The application was tested for:

- Adding pantry items.
- Displaying pantry items.
- Editing pantry items.
- Deleting pantry items.
- Persistence after closing and reopening the app.
- Empty pantry state.
- Empty suggested-recipes state.
- Missing required ingredients.
- Insufficient ingredient quantities.
- Singular and plural ingredient names.
- Compatible measurement units.
- Incompatible measurement units.
- Opening recipe details.
- Scrolling long recipe details.
- Saving and restoring the expiry-alert setting.
- Invalid form input.
- Navigation between application screens.

## GitHub Development History

The project was developed incrementally using Git and GitHub. Meaningful commits were made for:

- Initial Android project setup.
- Firebase connection.
- README documentation.
- Pantry screen layout.
- Pantry CRUD operations.
- RecyclerView implementation.
- Recipe models.
- Recipe seeding.
- Strict recipe matching.
- Recipe detail screen.
- Settings screen.
- Unit compatibility improvements.

## Assignment Information

Application: Smart Pantry Manager

Module: Mobile App Development 700

Language: Java

Database: Firebase Cloud Firestore

Student: Tyrone McCabe

Student Number: 402312907

GitHub Repository: https://github.com/MetDig58/Smart-Pantry-Manager
## Author

Student Name: Tyrone McCabe

Student Number: 402312907

Module: Mobile App Development 700