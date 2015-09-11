package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Utils;

public final class SearchManager {

    public static final String RECIPES_SEARCH_HISTORY = "recipes_search_history";
    public static final String INGREDIENTS_SEARCH_HISTORY = "ingredients_search_history";
    public static final String EXTRA_SEARCH_WHAT = "extra_search_what";
    private static final String SEARCH_HISTORY_PREFERENCE = "search_history_preference";
    private static SearchManager ourInstance;

    private SharedPreferences preferences;
    private String whatHistory;
    private ArrayList<String> cache;

    private SearchManager(Context context, String whatHistory) {
        this.preferences = context.getSharedPreferences(SEARCH_HISTORY_PREFERENCE, Context.MODE_PRIVATE);
        this.whatHistory = whatHistory;
        try {
            if (preferences.getAll().containsKey(whatHistory)) {
                this.cache = Utils.toStringList(new JSONArray(preferences.getString(whatHistory, "")));
            } else {
                this.cache = new ArrayList<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static SearchManager get(Context context, String whatHistory) {
        if (null != ourInstance && RECIPES_SEARCH_HISTORY.equalsIgnoreCase(whatHistory)) {
            return ourInstance;
        }

        return ourInstance = new SearchManager(context, whatHistory);
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
        preferences.edit().putString(whatHistory, Utils.stringListToJsonArray(cache).toString()).apply();
    }

}
