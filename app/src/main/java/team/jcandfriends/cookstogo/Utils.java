package team.jcandfriends.cookstogo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

public final class Utils {

    public static void persistBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public static boolean getPersistedBoolean(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defaultValue);
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showSnackbar(BaseActivity activity, String message) {
        Snackbar
                .make(activity.findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public static Bitmap getBitmapFromUrl() {
        return null;
    }

}
