package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Utils;

public final class IngredientSearchManager implements SearchManager<String> {

    public static final String INGREDIENTS_SEARCH_HISTORY = "ingredients_search_history";
    private static final String TAG = "IngredientSearchManager";
    private static IngredientSearchManager SOLE_INSTANCE;

    private final SharedPreferences mPreferences;
    private ArrayList<String> mCache;

    private IngredientSearchManager(Context context) {
        mPreferences = context.getSharedPreferences(IngredientSearchManager.INGREDIENTS_SEARCH_HISTORY, Context.MODE_PRIVATE);

        if (mPreferences.getAll().containsKey(IngredientSearchManager.INGREDIENTS_SEARCH_HISTORY)) {
            try {
                mCache = Utils.toStringList(new JSONArray(mPreferences.getString(IngredientSearchManager.INGREDIENTS_SEARCH_HISTORY, "")));
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            }
        } else {
            mCache = new ArrayList<>();
        }
    }

    public static IngredientSearchManager get(Context context) {
        if (null != SOLE_INSTANCE) {
            return SOLE_INSTANCE;
        }

        return SOLE_INSTANCE = new IngredientSearchManager(context);
    }

    public void add(String query) {
        if (mCache.contains(query)) {
            mCache.remove(query);
        }

        mCache.add(0, query);
        persist();
    }

    public void deleteAll() {
        mCache.clear();
        persist();
    }

    public ArrayList<String> getAll() {
        return mCache;
    }

    private void persist() {
        mPreferences.edit().putString(IngredientSearchManager.INGREDIENTS_SEARCH_HISTORY, Utils.stringListToJsonArray(mCache).toString()).apply();
    }

}
