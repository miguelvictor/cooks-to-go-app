package team.jcandfriends.cookstogo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

public final class Utils {

    /**
     * Persist a boolean to the default SharedPreferences of this app
     *
     * @param context The activity that requested to perform this operation
     * @param key     The name of the boolean value that is used to retrieve this value later
     * @param value   The boolean value that will be persisted
     */
    public static void persistBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * Get the boolean value that was persisted.
     *
     * @param context      The activity that requested to perform this operation
     * @param key          The name of the boolean value
     * @param defaultValue Returned if the key is not found
     * @return boolean value of the given key or the defaultValue
     */
    public static boolean getPersistedBoolean(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defaultValue);
    }

    /**
     * Persists a string to the default SharedPreferences
     *
     * @param context The activity that requested this action
     * @param key     The key of the value
     * @param value   The value that will be persisted
     */
    public static void persistString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply();
    }

    /**
     * Searches the default SharedPreferences if the given keys exist
     *
     * @param context The activity that requested this action
     * @param keys    Array of keys to search
     * @return true if and only if all keys exist otherwise false
     */
    public static boolean persistedKeyExists(Context context, String... keys) {
        Map objects = (Map) PreferenceManager.getDefaultSharedPreferences(context).getAll();

        for (String key : keys)
            if (!objects.containsKey(key))
                return false;

        return true;
    }

    /**
     * Retrieves the string with the given key on default SharedPreferences
     *
     * @param context      the activity that requested this action
     * @param key          The key
     * @param defaultValue The default value
     * @return The value of the given key if the key exists otherwise the defaultValue
     */
    public static String getPersistedString(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, defaultValue);
    }

    /**
     * Persists a JSONArray. Note that the persisted object in the default SharedPreferences
     * is not a JSONArray but a string that represents this JSONArray.
     *
     * @param context   the activity that requested this action
     * @param key       The key
     * @param jsonArray The value that will be persisted
     */
    public static void persistJSONArray(Context context, String key, JSONArray jsonArray) {
        persistString(context, key, jsonArray.toString());
    }

    /**
     * Returns the persisted JSONArray. If the key is not found, null is returned.
     *
     * @param context Activity Context
     * @param key     The key of the JSONArray
     * @return The persisted JSONArray or null if key is not found
     */
    public static JSONArray getPersistedJSONArray(Context context, String key) {
        String jsonArray = getPersistedString(context, key, "");
        try {
            return new JSONArray(jsonArray);
        } catch (JSONException e) {
            return null;
        }
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Wrapper function for the Snackbar.make()
     *
     * @param activity The activity that must have a coordinator layout
     * @param message The message that will be shown
     */
    public static void showSnackbar(BaseActivity activity, String message) {

        Snackbar
                .make(activity.findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    /**
     * Wrapper function for Log.d()
     *
     * @param message The message to be logged
     */
    public static void log(String message) {
        Log.d(Constants.APP_DEBUG, message);
    }

}
