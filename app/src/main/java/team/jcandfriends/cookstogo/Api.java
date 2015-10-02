package team.jcandfriends.cookstogo;

import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.inflector.English;

/**
 * Class that contains all keys in the JSON string returned by the rest api.
 */
public final class Api {

    public static final String RECIPES = "http://cookstogo.herokuapp.com/api/recipes/";
    public static final String RECOMMEND_RECIPES = "http://cookstogo.herokuapp.com/api/recipes/recommend";
    public static final String RECIPE_TYPES = "http://cookstogo.herokuapp.com/api/recipe-types/";
    public static final String INGREDIENTS = "http://cookstogo.herokuapp.com/api/ingredients/";
    public static final String INGREDIENT_TYPES = "http://cookstogo.herokuapp.com/api/ingredient-types/";

    /**
     * Recipe Recommendation
     */
    public static final String EXACT = "recipes";
    public static final String NEARLY_THERE = "nearly_there";

    /**
     * Pagination attrs
     */
    public static final String COUNT = "count";
    public static final String NEXT = "next";
    public static final String PREVIOUS = "previous";
    public static final String RESULTS = "results";

    /**
     * Relating to the Recipe Model
     */
    public static final String RECIPE_PK = "pk";
    public static final String RECIPE_ICON = "icon";
    public static final String RECIPE_BANNER = "banner";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_DESCRIPTION = "description";
    public static final String RECIPE_RECIPE_COMPONENTS = "recipe_components";
    public static final String RECIPE_STEPS = "steps";
    public static final String RECIPE_DURATION = "time_to_complete";
    public static final String RECIPE_DEFAULT_SERVING_SIZE = "default_serving_size";
    public static final String RECIPE_RATING = "rating";
    public static final String RECIPE_REVIEWS = "reviews";

    /**
     * Relating to the RecipeType Model
     */
    public static final String RECIPE_TYPE_NAME = "name";
    public static final String RECIPE_TYPE_RECIPES = "recipes";

    /**
     * Relating to the Ingredient Model
     */
    public static final String INGREDIENT_PK = "pk";
    public static final String INGREDIENT_NAME = "name";
    public static final String INGREDIENT_ICON = "icon";
    public static final String INGREDIENT_BANNER = "banner";
    public static final String INGREDIENT_DESCRIPTION = "description";

    /**
     * Relating to the IngredientType Model
     */
    public static final String INGREDIENT_TYPE_NAME = "name";
    public static final String INGREDIENT_TYPE_INGREDIENTS = "ingredients";

    /**
     * Relating to the RecipeComponent Model
     */
    public static final String RECIPE_COMPONENT_INGREDIENT = "ingredient";
    public static final String RECIPE_COMPONENT_QUANTITY = "quantity";
    public static final String RECIPE_COMPONENT_ADJECTIVE = "adjective";
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

    /**
     * Recommend Recipes
     */
    public static final String NEARLY_THERE_RECIPE = "recipe";
    public static final String NEARLY_THERE_MISSING_COUNT = "missing_count";

    /**
     * Returns the URL of the REST service which serves recipes by id
     *
     * @param recipeId The recipe to get
     * @return the URL of the REST service which serves recipes by id
     */
    public static String getRecipeUrl(int recipeId) {
        return Api.RECIPES + recipeId + '/';
    }

    /**
     * Returns the URL of the REST service which serves ingredients by id
     *
     * @param ingredientId The recipe to get
     * @return the URL of the REST service which serves recipes by id
     */
    public static String getIngredientUrl(int ingredientId) {
        return Api.INGREDIENTS + ingredientId + '/';
    }

    /**
     * Returns a human readable representation of the recipeComponent
     *
     * @param recipeComponent the recipeComponent
     * @return the human readable representation of the recipeComponent
     */
    public static String getIngredientReadableName(JSONObject recipeComponent) {
        try {
            StringBuilder sb = new StringBuilder();

            String quantity = recipeComponent.getString(RECIPE_COMPONENT_QUANTITY);
            String unitOfMeasure = recipeComponent.getJSONObject(RECIPE_COMPONENT_UNIT_OF_MEASURE).getString(UNIT_OF_MEASURE_NAME);
            String adjective = recipeComponent.getString(RECIPE_COMPONENT_ADJECTIVE);
            String ingredientName = recipeComponent.getJSONObject(RECIPE_COMPONENT_INGREDIENT).getString(INGREDIENT_NAME);

            int plural = 2;
            float qFloat = Float.parseFloat(quantity);

            Utils.log("Quantity: " + qFloat);
            if (qFloat == 1 || qFloat == 0.25 || qFloat == 0.5 || qFloat == 0.3333 || qFloat == 0.2 || qFloat == 0.1666 || qFloat == 0.125) {
                plural = 1;
            }

            if (quantity.matches("^\\d+(\\.0)?$")) { // if it's an integer
                sb.append(Integer.valueOf(quantity)).append(" ");
            } else { // if it's a float
                sb.append(Utils.fractionize(quantity)).append(" ");
            }

            if (!unitOfMeasure.equals("generic")) {
                sb.append(English.plural(unitOfMeasure, plural)).append(" of ");
            }

            if (!adjective.isEmpty()) {
                sb.append(adjective).append(" ");
            }

            return sb.append(ingredientName).toString().toLowerCase();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a human readable representation of the recipe duration which is in minutes by default.
     *
     * @param duration in minutes, should be greater than zero
     * @return the formatted duration of recipe preparation
     */
    public static String normalizeRecipeDuration(int duration) {
        StringBuilder result = new StringBuilder();

        int hours = duration / 60;
        int minutes = duration % 60;

        if (hours != 0 && minutes != 0) {
            result.append(hours).append(" ").append(English.plural("hour", hours)).append(" and ").append(minutes).append(" ").append(English.plural("minute", minutes));
        } else if (hours != 0) {
            result.append(hours).append(" ").append(English.plural("hour", hours));
        } else {
            result.append(minutes).append(" ").append(English.plural("minute", minutes));
        }

        return result.toString();
    }

}
