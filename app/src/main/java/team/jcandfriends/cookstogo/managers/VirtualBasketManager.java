package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Utils;

public class VirtualBasketManager {

    /**
     *
     */
    public static final String PERSISTENT_VIRTUAL_BASKET_ITEMS = "persistent_virtual_basket_items";
    /**
     * The Log tag
     */
    private static final String TAG = "VirtualBasketManager";
    /**
     * The name of the SharedPreferences that contains the caches of this class
     */
    private static final String VIRTUAL_BASKET_CACHE = "virtual_basket_cache";
    /**
     * Singleton instance
     */
    private static VirtualBasketManager ourInstance;

    private ArrayList<JSONObject> cachedItems;
    private SharedPreferences preferences;

    private VirtualBasketManager(Context context) {
        preferences = context.getSharedPreferences(VIRTUAL_BASKET_CACHE, Context.MODE_PRIVATE);
        String items = preferences.getString(PERSISTENT_VIRTUAL_BASKET_ITEMS, null);

        if (null == items) {
            cachedItems = new ArrayList<>();
        } else {
            try {
                cachedItems = Utils.jsonArrayToList(new JSONArray(items));
            } catch (JSONException e) {
                Log.e(TAG, "virtual basket items can't be parsed to a JSONArray");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the singleton instance
     *
     * @param context the context
     * @return the singleton instance
     */
    public static VirtualBasketManager get(Context context) {
        if (ourInstance == null) {
            ourInstance = new VirtualBasketManager(context);
        }
        return ourInstance;
    }

    public void add(JSONObject ingredient) throws IllegalStateException {
        if (cachedItems.contains(ingredient)) {
            throw new IllegalStateException(ingredient.optString(Api.INGREDIENT_TYPE_NAME) + " is already added.");
        }
        cachedItems.add(ingredient);
        persist();
    }

    public void remove(JSONObject ingredient) {
        cachedItems.remove(ingredient);
        persist();
    }

    public ArrayList<JSONObject> getAll() {
        return cachedItems;
    }

    private void persist() {
        preferences.edit().putString(PERSISTENT_VIRTUAL_BASKET_ITEMS, Utils.listToJsonArray(cachedItems).toString()).apply();
    }

    public boolean isAlreadyAdded(JSONObject ingredient) {
        int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);

        for (JSONObject i : cachedItems) {
            if (ingredientId == i.optInt(Api.INGREDIENT_PK)) {
                return true;
            }
        }

        return false;
    }

    public void deleteAll() {
        cachedItems.clear();
        preferences.edit().remove(PERSISTENT_VIRTUAL_BASKET_ITEMS).apply();
    }
}
