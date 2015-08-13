package team.jcandfriends.cookstogo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Data {

    public static final String PERSISTENT_RECIPE_TYPES = "persistent_recipe_types";
    public static final String PERSISTENT_INGREDIENT_TYPES = "persistent_ingredient_types";

    private static JSONArray recipeTypes;
    private static JSONArray ingredientTypes;

    public static boolean isSafeToProceed(Context context) {
        return Utils.persistedKeyExists(context, PERSISTENT_RECIPE_TYPES, PERSISTENT_INGREDIENT_TYPES);
    }

    public static JSONArray getRecipeTypes(Context context) {
        if (recipeTypes == null) {
            if (Utils.persistedKeyExists(context, PERSISTENT_RECIPE_TYPES)) {
                recipeTypes = Utils.getPersistedJSONArray(context, PERSISTENT_RECIPE_TYPES);
            } else {
                throw new RuntimeException("Attempt to get recipeTypes but recipeTypes was not initialized maybe because initializeData() was not called or initializeData() has thrown a JSONException");
            }
        }

        return recipeTypes;
    }

    public static JSONArray getIngredientTypes(Context context) {
        if (ingredientTypes == null) {
            if (Utils.persistedKeyExists(context, PERSISTENT_INGREDIENT_TYPES)) {
                ingredientTypes = Utils.getPersistedJSONArray(context, PERSISTENT_INGREDIENT_TYPES);
            } else {
                throw new RuntimeException("Attempt to get ingredientTypes but ingredientTypes was not initialized maybe because initializeData() was not called or initializeData() has thrown a JSONException");
            }
        }

        return ingredientTypes;
    }

    public static void initializeRecipeTypes(Context context, JSONObject object) throws JSONException {
        recipeTypes = object.getJSONArray("results");
        Utils.persistJSONArray(context, PERSISTENT_RECIPE_TYPES, recipeTypes);
    }

    public static void initializeIngredientTypes(Context context, JSONObject object) throws JSONException {
        ingredientTypes = object.getJSONArray(Api.RECIPETYPE_RESULTS);
        Utils.persistJSONArray(context, PERSISTENT_INGREDIENT_TYPES, ingredientTypes);
    }

}
