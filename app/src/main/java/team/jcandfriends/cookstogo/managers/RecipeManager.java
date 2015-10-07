package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.JSONGrabber;
import team.jcandfriends.cookstogo.Utils;

public class RecipeManager {

    /**
     * The Log tag
     */
    private static final String TAG = "RecipeManager";

    /**
     * The name of the SharedPreferences that contains the caches of this class
     */
    private static final String RECIPE_CACHE = "recipe_cache";

    /**
     * The key of the cached recipe types
     */
    private static final String PERSISTENT_RECIPE_TYPES = "persistent_recipe_types";

    /**
     * The prefix of each recipe that was cached
     */
    private static final String RECIPE_CACHE_PREFIX = "recipe";

    /**
     * Singleton instance
     */
    private static RecipeManager SOLE_INSTANCE;

    /**
     * The SharedPreferences that contains the caches of this class
     */
    private final SharedPreferences mPreferences;

    private RecipeManager(Context context) {
        mPreferences = context.getSharedPreferences(RecipeManager.RECIPE_CACHE, Context.MODE_PRIVATE);
    }

    /**
     * Returns the singleton instance
     *
     * @param context the context
     * @return the singleton instance
     */
    public static RecipeManager get(Context context) {
        if (SOLE_INSTANCE == null) {
            SOLE_INSTANCE = new RecipeManager(context);
        }
        return SOLE_INSTANCE;
    }

    /**
     * Checks the cache if the previously fetched recipe types were cached
     *
     * @return true if recipe types cache is not empty, false otherwise
     */
    public boolean hasCachedRecipeTypes() {
        return mPreferences.getAll().containsKey(RecipeManager.PERSISTENT_RECIPE_TYPES);
    }

    /**
     * Caches the given JSONArray
     *
     * @param recipeTypes the JSONArray to cache
     */
    public void cacheRecipeTypes(JSONArray recipeTypes) {
        mPreferences.edit().putString(RecipeManager.PERSISTENT_RECIPE_TYPES, recipeTypes.toString()).apply();
    }

    /**
     * Returns the cached recipe types
     *
     * @return the cached recipe types as JSONArray
     */
    public JSONArray getCachedRecipeTypes() {
        String recipeTypesAsString = mPreferences.getString(RecipeManager.PERSISTENT_RECIPE_TYPES, "");
        try {
            return new JSONArray(recipeTypesAsString);
        } catch (JSONException e) {
            Log.e(RecipeManager.TAG, "JSONException : The recipeTypesAsString can't be parsed as a valid JSONArray", e);
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Removes the cached recipe types
     */
    public void clearCachedRecipeTypes() {
        mPreferences.edit().remove(RecipeManager.PERSISTENT_RECIPE_TYPES).apply();
    }

    /**
     * Determines if the the recipe with the given id exists in the cache
     *
     * @param recipeId the id of the recipe to search
     * @return true if the recipe is found, false otherwise
     */
    public boolean hasCachedRecipe(int recipeId) {
        return mPreferences.getAll().containsKey(RecipeManager.RECIPE_CACHE_PREFIX + recipeId);
    }

    /**
     * Caches a recipe
     *
     * @param recipe the recipe to cache
     */
    public void cacheRecipe(JSONObject recipe) {
        try {
            mPreferences.edit().putString(RecipeManager.RECIPE_CACHE_PREFIX + recipe.getInt(Api.RECIPE_PK), recipe.toString()).apply();
        } catch (JSONException e) {
            Log.e(RecipeManager.TAG, "JSONException : The passed JSONObject recipe doesn't contain a '" + Api.RECIPE_PK + "'", e);
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Returns a JSONObject that contains the cached recipe
     *
     * @param recipeId the id of the recipe to get
     * @return the recipe
     */
    public JSONObject getCachedRecipe(int recipeId) {
        String recipeAsString = this.mPreferences.getString(RecipeManager.RECIPE_CACHE_PREFIX + recipeId, "");
        try {
            return new JSONObject(recipeAsString);
        } catch (JSONException e) {
            Log.e(RecipeManager.TAG, "JSONException : The recipeAsString can't be parsed as a valid JSONObject", e);
            throw new RuntimeException("Cannot proceed anymore");
        }
    }

    /**
     * Removes a recipe from the cache
     *
     * @param recipeId the id of the recipe to remove
     */
    public void clearCachedRecipe(int recipeId) {
        mPreferences.edit().remove(RecipeManager.RECIPE_CACHE_PREFIX + recipeId).apply();
    }

    /**
     * Grabs the recipe on the cloud server
     *
     * @param recipeId  the id of the recipe to get
     * @param callbacks the callbacks that will be invoked on success and on failure
     */
    public void fetch(final int recipeId, final RecipeManager.Callbacks callbacks) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONGrabber grabber = new JSONGrabber(Api.RECIPES + recipeId);
                    return grabber.grab();
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "JSONException", e);
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
     * Sends a rating to the server
     *
     * @param recipeId  the id of the recipe to be rated
     * @param rating    the rating on a scale of 1 - 5
     * @param callbacks the callbacks that will be invoked in cases of success and error
     */
    public void rate(final Context context, int recipeId, final int rating, final RecipeManager.Callbacks callbacks) {
        if (rating < 0 || rating > 5) {
            throw new RuntimeException("Recipe ID must not be in the range of (0-5)");
        }

        final String urlAsString = Api.RECIPES + recipeId + "/rate/";

        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONObject response = null;
                    URL url = new URL(urlAsString);
                    Log.i(TAG, "Attempting to connect to " + url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    String requestBody = String.format("rating=%d&mac=%s", rating, URLEncoder.encode(Utils.getMacAddress(context), "UTF-8"));
                    byte[] requestBodyAsBytes = requestBody.getBytes();

                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
                    connection.setReadTimeout(Constants.READ_TIMEOUT);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("charset", "UTF-8");
                    connection.setRequestProperty("Content-Length", Integer.toString(requestBodyAsBytes.length));
                    connection.setDoOutput(true);

                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                    os.write(requestBodyAsBytes);

                    Log.i(TAG, "Request body : " + requestBody);

                    connection.connect();

                    int statusCode = connection.getResponseCode();

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    Log.i(TAG, "Connection to " + url + " was established with a status code of " + statusCode);
                    Log.i(TAG, "Response body: " + builder);

                    try {
                        response = new JSONObject(builder.toString());
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException", e);
                    }
                    return response;
                } catch (MalformedURLException e) {
                    Log.e(RecipeManager.TAG, "Could not parse the url for rating a recipe : " + urlAsString, e);
                } catch (IOException e) {
                    Log.e(RecipeManager.TAG, "Could not open connection from URL", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject response) {
                super.onPostExecute(response);

                if (null != response) {
                    callbacks.onSuccess(response);
                } else {
                    callbacks.onFailure();
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
    public void search(final String query, final RecipeManager.Callbacks callbacks) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query must not be null and empty");
        }

        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONGrabber grabber = new JSONGrabber(Api.RECIPES + "?search=" + URLEncoder.encode(query, "UTF-8"));
                    return grabber.grab();
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "JSONException", e);
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
     * containing the count, next, previous, and the actual results which are the recipes that contain
     * ingredients from the given ingredients parameter.
     *
     * @param ingredients the list of ingredients
     * @param callbacks   the callbacks that will be invoked in success or failure event
     */
    public void recommendRecipes(ArrayList<JSONObject> ingredients, final RecipeManager.Callbacks callbacks) {
        if (ingredients.size() < 1)
            throw new IllegalArgumentException("ingredients length < 1 : " + ingredients.size());

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        sb.append(Api.RECOMMEND_RECIPES).append("?q=");
        for (JSONObject ingredient : ingredients) {
            if (first) {
                sb.append(ingredient.optInt(Api.INGREDIENT_PK));
                first = false;
            } else {
                sb.append(",").append(ingredient.optInt(Api.INGREDIENT_PK));
            }
        }

        final String url = sb.toString();

        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    JSONGrabber grabber = new JSONGrabber(url);
                    return grabber.grab();
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "IOException | JSONException", e);
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

    public void clearCachedRecipes() {
        final char firstChar = RECIPE_CACHE_PREFIX.charAt(0);
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

    public interface Callbacks {
        void onSuccess(JSONObject result);

        void onFailure();
    }
}
