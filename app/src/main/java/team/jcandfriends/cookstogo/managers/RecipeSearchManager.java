package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Utils;

public final class RecipeSearchManager implements SearchManager<String> {

    public static final String RECIPES_SEARCH_HISTORY = "recipes_search_history";
    public static final String EXTRA_SEARCH_WHAT = "extra_search_what";

    private static RecipeSearchManager ourInstance;

    private SharedPreferences preferences;
    private ArrayList<String> cache;

    private RecipeSearchManager(Context context) {
        this.preferences = context.getSharedPreferences(RECIPES_SEARCH_HISTORY, Context.MODE_PRIVATE);

        try {
            if (preferences.getAll().containsKey(RECIPES_SEARCH_HISTORY)) {
                this.cache = Utils.toStringList(new JSONArray(preferences.getString(RECIPES_SEARCH_HISTORY, "")));
            } else {
                this.cache = new ArrayList<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RecipeSearchManager get(Context context) {
        if (null != ourInstance) {
            return ourInstance;
        }

        return ourInstance = new RecipeSearchManager(context);
    }

    public void add(String query) {
        if (cache.contains(query)) {
            cache.remove(query);
        }

        cache.add(0, query);
        persist();
    }

    public void deleteAll() {
        cache.clear();
        persist();
    }

    public ArrayList<String> getAll() {
        return cache;
    }

    private void persist() {
        preferences.edit().putString(RECIPES_SEARCH_HISTORY, Utils.stringListToJsonArray(cache).toString()).apply();
    }

}
