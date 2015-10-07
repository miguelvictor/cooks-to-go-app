package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Set;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.JSONGrabber;

public class IngredientManager {

    /**
     * The Log tag
     */
    private static final String TAG = "IngredientManager";

    /**
     * The name of the SharedPreferences that contains the caches of this class
     */
    private static final String INGREDIENT_CACHE = "ingredient_cache";

    /**
     * The key of the cached ingredient types
     */
    private static final String PERSISTENT_INGREDIENT_TYPES = "persistent_ingredient_types";

    /**
     * The prefix of each ingredient that was cached
     */
    private static final String INGREDIENT_CACHE_PREFIX = "ingredient";

    /**
     * The key of the saved ingredient. Used in saving the last selected ingredient in which the user hasn't created any virtual baskets yet
     */
    private static final String SAVED_INGREDIENT = "saved_ingredient";

    /**
     * Singleton instance
     */
    private static IngredientManager SOLE_INSTANCE;

    /**
     * The SharedPreferences that contains the caches of this class
     */
    private final SharedPreferences mPreferences;

    private IngredientManager(Context context) {
        mPreferences = context.getSharedPreferences(IngredientManager.INGREDIENT_CACHE, Context.MODE_PRIVATE);
    }

    /**
     * Returns the singleton instance
     *
     * @param context the context
     * @return the singleton instance
     */
    public static IngredientManager get(Context context) {
        if (SOLE_INSTANCE == null) {
            SOLE_INSTANCE = new IngredientManager(context);
        }
        return SOLE_INSTANCE;
    }

    /**
     * Checks the cache if the previously fetched recipe types were cached
     *
     * @return true if recipe types cache is not empty, false otherwise
     */
    public boolean hasCachedIngredientTypes() {
        return mPreferences.getAll().containsKey(IngredientManager.PERSISTENT_INGREDIENT_TYPES);
    }

    /**
     * Caches the given JSONArray
     *
     * @param recipeTypes the JSONArray to cache
     */
    public void cacheIngredientTypes(JSONArray recipeTypes) {
        mPreferences.edit().putString(IngredientManager.PERSISTENT_INGREDIENT_TYPES, recipeTypes.toString()).apply();
    }

    /**
     * Returns the cached recipe types
     *
     * @return the cached recipe types as JSONArray
     */
    public JSONArray getCachedIngredientTypes() {
        String ingredientTypesAsString = mPreferences.getString(IngredientManager.PERSISTENT_INGREDIENT_TYPES, "");
        try {
            return new JSONArray(ingredientTypesAsString);
        } catch (JSONException e) {
            Log.e(IngredientManager.TAG, "JSONException : The ingredientTypesAsString can't be parsed as a valid JSONArray", e);
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Removes the cached recipe types
     */
    public void clearCachedIngredientTypes() {
        mPreferences.edit().remove(IngredientManager.PERSISTENT_INGREDIENT_TYPES).apply();
    }

    /**
     * Determines if the the recipe with the given id exists in the cache
     *
     * @param recipeId the id of the recipe to search
     * @return true if the recipe is found, false otherwise
     */
    public boolean hasCachedIngredient(int recipeId) {
        return mPreferences.getAll().containsKey(IngredientManager.INGREDIENT_CACHE_PREFIX + recipeId);
    }

    /**
     * Caches a ingredient
     *
     * @param ingredient the ingredient to cache
     */
    public void cacheIngredient(JSONObject ingredient) {
        try {
            mPreferences.edit().putString(IngredientManager.INGREDIENT_CACHE_PREFIX + ingredient.getInt(Api.INGREDIENT_PK), ingredient.toString()).apply();
        } catch (JSONException e) {
            Log.e(IngredientManager.TAG, "JSONException : The passed JSONObject ingredient doesn't contain a '" + Api.INGREDIENT_PK + "'", e);
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Returns a JSONObject that contains the cached recipe
     *
     * @param ingredientId the id of the recipe to get
     * @return the recipe
     */
    public JSONObject getCachedIngredient(int ingredientId) {
        String ingredientAsString = mPreferences.getString(IngredientManager.INGREDIENT_CACHE_PREFIX + ingredientId, "");
        try {
            return new JSONObject(ingredientAsString);
        } catch (JSONException e) {
            Log.e(IngredientManager.TAG, "JSONException : The ingredientAsString can't be parsed as a valid JSONObject", e);
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Removes a recipe from the cache
     *
     * @param ingredientId the id of the recipe to remove
     */
    public void clearCachedIngredient(int ingredientId) {
        mPreferences.edit().remove(IngredientManager.INGREDIENT_CACHE_PREFIX + ingredientId).apply();
    }

    public void fetch(final int ingredientId, final IngredientManager.Callbacks callbacks) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONGrabber grabber = new JSONGrabber(Api.INGREDIENTS + ingredientId);
                    return grabber.grab();
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "IOException | JSONException", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);

                if (null == result) {
                    callbacks.onFailure();
                } else {
                    callbacks.onSuccess(result);
                }
            }
        }.execute();
    }

    /**
     * Starts an asynchronous request to the backend server that will return a resulting JSONObject
     * containing the count, next, previous, and the actual results which are the recipes that
     * matches the query.
     *
     * @param query     the query
     * @param callbacks the callbacks that will be invoked in success or failure event
     */
    public void search(final String query, final IngredientManager.Callbacks callbacks) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query must not be null and empty");
        }

        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONGrabber grabber = new JSONGrabber(Api.INGREDIENTS + "?search=" + URLEncoder.encode(query, "UTF-8"));
                    return grabber.grab();
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "IOException | JSONException", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                if (jsonObject == null) {
                    callbacks.onFailure();
                } else {
                    callbacks.onSuccess(jsonObject);
                }
            }
        }.execute();
    }

    /**
     * Removes all ingredients from the cache
     */
    public void clearCachedIngredients() {
        final char firstChar = INGREDIENT_CACHE_PREFIX.charAt(0);
        final Set<String> keys = mPreferences.getAll().keySet();

        if (keys.size() > 1) {
            SharedPreferences.Editor editor = mPreferences.edit();

            for (String key : keys) {
                if (firstChar == key.charAt(0)) {
                    editor.remove(key);
                }
            }

            editor.apply();
        }
    }

    /**
     * Saves the ingredient
     */
    public void saveIngredient(JSONObject ingredient) {
        mPreferences.edit().putString(SAVED_INGREDIENT, ingredient.toString()).apply();
    }

    /**
     * Returns the saved ingredient
     *
     * @return the saved ingredient or null if it doesn't exist
     */
    public JSONObject getSavedIngredient() {
        String savedIngredientAsString = mPreferences.getString(SAVED_INGREDIENT, null);
        try {
            return new JSONObject(savedIngredientAsString);
        } catch (JSONException e) {
            Log.e(TAG, "Exception while parsing the saved ingredient : " + savedIngredientAsString, e);
            return null;
        }
    }

    public void forgetSavedIngredient() {
        mPreferences.edit().remove(SAVED_INGREDIENT).apply();
    }

    public interface Callbacks {
        void onSuccess(JSONObject result);

        void onFailure();
    }
}
