package team.jcandfriends.cookstogo;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The manager for storing and getting cached ingredients and recipes.
 */
public final class Data {

    public static final String PERSISTENT_RECIPE_TYPES = "persistent_recipe_types";
    public static final String PERSISTENT_INGREDIENT_TYPES = "persistent_ingredient_types";

    public static final String RECIPE_CACHE = "recipe_cache";
    public static final String RECIPE_CACHE_PREFIX = "recipe";
    public static final String INGREDIENT_CACHE = "ingredient_cache";
    public static final String INGREDIENT_CACHE_PREFIX = "ingredient";

    private static JSONArray recipeTypes;
    private static JSONArray ingredientTypes;

    /**
     * Returns true if the recipe and ingredient types (including the corresponding data in overview form) are already cached.
     *
     * @param context The activity
     * @return true or false XD
     */
    public static boolean isSafeToProceed(Context context) {
        return Utils.persistedKeyExists(context, PERSISTENT_RECIPE_TYPES, PERSISTENT_INGREDIENT_TYPES);
    }

    public static void initializeIngredientTypes(Context context, JSONObject object) throws JSONException {
        ingredientTypes = object.getJSONArray(Api.RESULTS);
        Utils.persistJSONArray(context, PERSISTENT_INGREDIENT_TYPES, ingredientTypes);
    }

    public static boolean hasCachedRecipe(Context context, int recipeId) {
        SharedPreferences recipeCache = context.getSharedPreferences(RECIPE_CACHE, Context.MODE_PRIVATE);
        return recipeCache.getAll().containsKey(RECIPE_CACHE_PREFIX + recipeId);
    }

    public static void cacheRecipe(Context context, JSONObject recipe) {
        SharedPreferences recipeCache = context.getSharedPreferences(RECIPE_CACHE, Context.MODE_PRIVATE);
        recipeCache
                .edit()
                .putString(RECIPE_CACHE_PREFIX + recipe.optInt(Api.RECIPE_PK), recipe.toString())
                .apply();
    }

    public static JSONObject getCachedRecipe(Context context, int recipeId) throws JSONException {
        SharedPreferences recipeCache = context.getSharedPreferences(RECIPE_CACHE, Context.MODE_PRIVATE);
        return new JSONObject(recipeCache.getString(RECIPE_CACHE_PREFIX + recipeId, ""));
    }

    public static boolean hasCachedIngredient(Context context, int ingredientId) {
        SharedPreferences ingredientCache = context.getSharedPreferences(INGREDIENT_CACHE, Context.MODE_PRIVATE);
        return ingredientCache.getAll().containsKey(INGREDIENT_CACHE_PREFIX + ingredientId);
    }

    /**
     * Caches the ingredient.
     *
     * @param context    The activity
     * @param ingredient The ingredient to be cached
     * @return true if caching is successful, otherwise, false
     */
    public static boolean cacheIngredient(Context context, JSONObject ingredient) {
        SharedPreferences ingredientCache = context.getSharedPreferences(INGREDIENT_CACHE, Context.MODE_PRIVATE);
        try {
            ingredientCache
                    .edit()
                    .putString(INGREDIENT_CACHE_PREFIX + ingredient.getInt(Api.INGREDIENT_PK), ingredient.toString())
                    .apply();
        } catch (JSONException e) {
            Utils.log("JSONException : " + e.getMessage());
            return false;
        }
        return true;
    }

    public static JSONObject getCachedIngredient(Context context, int ingredientId) throws JSONException {
        SharedPreferences ingredientCache = context.getSharedPreferences(INGREDIENT_CACHE, Context.MODE_PRIVATE);
        return new JSONObject(ingredientCache.getString(INGREDIENT_CACHE_PREFIX + ingredientId, ""));
    }

}
