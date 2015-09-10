package team.jcandfriends.cookstogo;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public final class SearchManager {

    public static final String SEARCH_HISTORY_PREFERENCE = "search_history_preference";
    public static final String SEARCH_HISTORY = "search_history";

    private static JSONArray searchHistory;

    private static void init(Context context) {
        if (null == searchHistory) {
            SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY_PREFERENCE, Context.MODE_PRIVATE);
            try {
                searchHistory = new JSONArray(sp.getString(SEARCH_HISTORY, ""));
            } catch (JSONException e) {
                searchHistory = new JSONArray();
            }
        }
    }

    private static void persist(Context context) {
        context.getSharedPreferences(SEARCH_HISTORY_PREFERENCE, Context.MODE_PRIVATE)
                .edit()
                .putString(SEARCH_HISTORY, searchHistory.toString())
                .apply();
    }

    public static ArrayList<String> getHistory(Context context) {
        init(context);
        return Utils.toStringList(searchHistory);
    }

    public static void add(Context context, String query) {
        init(context);
        searchHistory.put(query);
        persist(context);
    }

    public static void deleteAll(Context context) {
        init(context);
        searchHistory = new JSONArray();
        persist(context);
    }

}
