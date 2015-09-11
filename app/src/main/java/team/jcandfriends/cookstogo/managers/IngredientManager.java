package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
     * Singleton instance
     */
    private static IngredientManager ourInstance;

    /**
     * The SharedPreferences that contains the caches of this class
     */
    private SharedPreferences preferences;

    private IngredientManager(Context context) {
        preferences = context.getSharedPreferences(INGREDIENT_CACHE, Context.MODE_PRIVATE);
    }

    /**
     * Returns the singleton instance
     *
     * @param context the context
     * @return the singleton instance
     */
    public static IngredientManager get(Context context) {
        if (ourInstance == null) {
            ourInstance = new IngredientManager(context);
        }
        return ourInstance;
    }

    /**
     * Checks the cache if the previously fetched recipe types were cached
     *
     * @return true if recipe types cache is not empty, false otherwise
     */
    public boolean hasCachedIngredientTypes() {
        return preferences.getAll().containsKey(PERSISTENT_INGREDIENT_TYPES);
    }

    /**
     * Caches the given JSONArray
     *
     * @param recipeTypes the JSONArray to cache
     */
    public void cacheIngredientTypes(JSONArray recipeTypes) {
        preferences.edit().putString(PERSISTENT_INGREDIENT_TYPES, recipeTypes.toString()).apply();
    }

    /**
     * Returns the cached recipe types
     *
     * @return the cached recipe types as JSONArray
     */
    public JSONArray getCachedIngredientTypes() {
        String ingredientTypesAsString = preferences.getString(PERSISTENT_INGREDIENT_TYPES, "");
        try {
            return new JSONArray(ingredientTypesAsString);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException : The ingredientTypesAsString can't be parsed as a valid JSONArray");
            e.printStackTrace();
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Removes the cached recipe types
     */
    public void clearCachedIngredientTypes() {
        preferences.edit().remove(PERSISTENT_INGREDIENT_TYPES).apply();
    }

    /**
     * Determines if the the recipe with the given id exists in the cache
     *
     * @param recipeId the id of the recipe to search
     * @return true if the recipe is found, false otherwise
     */
    public boolean hasCachedIngredient(int recipeId) {
        return preferences.getAll().containsKey(INGREDIENT_CACHE_PREFIX + recipeId);
    }

    /**
     * Caches a ingredient
     *
     * @param ingredient the ingredient to cache
     */
    public void cacheIngredient(JSONObject ingredient) {
        try {
            preferences.edit().putString(INGREDIENT_CACHE_PREFIX + ingredient.getInt(Api.INGREDIENT_PK), ingredient.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "JSONException : The passed JSONObject ingredient doesn't contain a '" + Api.INGREDIENT_PK + "'");
            e.printStackTrace();
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
        String ingredientAsString = preferences.getString(INGREDIENT_CACHE_PREFIX + ingredientId, "");
        try {
            return new JSONObject(ingredientAsString);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException : The ingredientAsString can't be parsed as a valid JSONObject");
            e.printStackTrace();
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Removes a recipe from the cache
     *
     * @param ingredientId the id of the recipe to remove
     */
    public void clearCachedIngredient(int ingredientId) {
        preferences.edit().remove(INGREDIENT_CACHE_PREFIX + ingredientId).apply();
    }

    public void fetch(final int ingredientId, final Callbacks callbacks) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONGrabber grabber = new JSONGrabber(Api.INGREDIENTS + ingredientId);
                    return grabber.grab();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
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
    public void search(String query, final Callbacks callbacks) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query must not be null and empty");
        }

        final String url = Api.INGREDIENTS + "?search=" + query;

        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONGrabber grabber = new JSONGrabber(url);
                    return grabber.grab();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
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

    public interface Callbacks {
        void onSuccess(JSONObject result);

        void onFailure();
    }
}
