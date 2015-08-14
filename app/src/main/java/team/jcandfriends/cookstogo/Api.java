package team.jcandfriends.cookstogo;

import java.net.MalformedURLException;

/**
 * Class that contains all keys in the JSON string returned by the axasas.
 */
public final class Api {

    public static final String RECIPES = "http://cookstogo.herokuapp.com/api/recipes/";
    public static final String RECIPE_TYPES = "http://cookstogo.herokuapp.com/api/recipe-types/";
    public static final String INGREDIENTS = "http://cookstogo.herokuapp.com/api/ingredients/";
    public static final String INGREDIENT_TYPES = "http://cookstogo.herokuapp.com/api/ingredient-types/";

    /**
     * Relating to the Recipe Model
     */
    public static final String RECIPE_PK = "pk";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_DESCRIPTION = "description";
    public static final String RECIPE_RECIPE_COMPONENTS = "recipe_components";
    public static final String RECIPE_STEPS = "steps";

    /**
     * Relating to the RecipeType Model
     */
    public static final String RECIPE_TYPE_NAME = "name";
    public static final String RECIPE_TYPE_RECIPES = "recipes";
    public static final String RECIPE_TYPE_RESULTS = "results";

    /**
     * Relating to the Ingredient Model
     */
    public static final String INGREDIENT_NAME = "name";

    /**
     * Relating to the IngredientType Model
     */
    public static final String INGREDIENTTYPE_NAME = "name";
    public static final String INGREDIENTTYPE_INGREDIENTS = "ingredients";

    /**
     * Relating to the RecipeComponent Model
     */
    public static final String RECIPE_COMPONENT_INGREDIENT = "ingredient";
    public static final String RECIPE_COMPONENT_QUANTITY = "quantity";
    public static final String RECIPE_COMPONENT_UNIT_OF_MEASURE = "unit_of_measure";

    /**
     * Relating to the UnitOfMeasure Model
     */
    public static final String UNIT_OF_MEASURE_NAME = "name";

    /**
     * Relating to the Step Model
     */
    public static final String STEP_INSTRUCTION = "instruction";
    public static final String STEP_SEQUENCE = "sequence";

    public static String getRecipeUrl(int recipeId) throws MalformedURLException {
        return RECIPES + recipeId + '/';
    }

}
