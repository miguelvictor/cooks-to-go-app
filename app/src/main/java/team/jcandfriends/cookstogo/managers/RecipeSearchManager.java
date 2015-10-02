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

    private final SharedPreferences preferences;
    private ArrayList<String> cache;

    private RecipeSearchManager(Context context) {
        preferences = context.getSharedPreferences(RecipeSearchManager.RECIPES_SEARCH_HISTORY, Context.MODE_PRIVATE);

        try {
            if (this.preferences.getAll().containsKey(RecipeSearchManager.RECIPES_SEARCH_HISTORY)) {
                cache = Utils.toStringList(new JSONArray(this.preferences.getString(RecipeSearchManager.RECIPES_SEARCH_HISTORY, "")));
            } else {
                cache = new ArrayList<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RecipeSearchManager get(Context context) {
        if (null != RecipeSearchManager.ourInstance) {
            return RecipeSearchManager.ourInstance;
        }

        return RecipeSearchManager.ourInstance = new RecipeSearchManager(context);
    }

    public void add(String query) {
        if (this.cache.contains(query)) {
            this.cache.remove(query);
        }

        this.cache.add(0, query);
        this.persist();
    }

    public void deleteAll() {
        this.cache.clear();
        this.persist();
    }

    public ArrayList<String> getAll() {
        return this.cache;
    }

    private void persist() {
        this.preferences.edit().putString(RecipeSearchManager.RECIPES_SEARCH_HISTORY, Utils.stringListToJsonArray(this.cache).toString()).apply();
    }

}
