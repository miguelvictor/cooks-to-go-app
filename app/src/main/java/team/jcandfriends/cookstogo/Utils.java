package team.jcandfriends.cookstogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import team.jcandfriends.cookstogo.interfaces.TabsToolbarGettable;
import team.jcandfriends.cookstogo.interfaces.ToolbarGettable;

/**
 * Class that contains all utility methods for common tasks.
 */
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
        String toPersist = jsonArray == null ? "" : jsonArray.toString();
        persistString(context, key, toPersist);
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

    /**
     * Checks if the device is currently connected to any network. The function name hasInternet
     * does not conform to the function of this function XD because it doesn't check if it is
     * indeed connected to the internet.
     *
     * @param context The activity
     * @return true of the device is currently connected to any network, otherwise, false
     */
    public static boolean hasInternet(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Wrapper function for the Snackbar.make()
     *
     * @param activity The activity that must have a coordinator layout
     * @param message  The message that will be shown
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

    /**
     * Decorates the android.support.v7.widget.Toolbar and the android.support.design.widget.TabLayout
     * to a swatch generated based on the bitmap parameter. Note that the id of the toolbar must be
     * 'toolbar' and the id of the TabLayout must be 'tab_layout' in order for this method to work.
     * This method uses reflection because Google hasn't provided a public method for changing the
     * tabLayout's indicator color programmatically even though a private method can be seen if one
     * can see the decompiled version of the TabLayout class. This method doesn't cache the Method object,
     * use only when you want to sacrifice performance for appearance.
     *
     * @param activity The activity where the Toolbar and the TabLayout lives
     * @param bitmap   The bitmap where the swatch will be based on
     */
    public static void decorateToolbarAndTabs(Activity activity, Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
        Palette.Swatch vibrantLightSwatch = p.getLightVibrantSwatch();
        Palette.Swatch vibrantDarkSwatch = p.getDarkVibrantSwatch();

        if (vibrantSwatch != null && vibrantLightSwatch != null && vibrantDarkSwatch != null) {
            int primaryColor = vibrantSwatch.getRgb();
            Toolbar toolbar = ((TabsToolbarGettable) activity).getToolbar();
            toolbar.setBackgroundColor(primaryColor);
            TabLayout tabs = ((TabsToolbarGettable) activity).getTabLayout();
            tabs.setBackgroundColor(primaryColor);
            setTabIndicatorColor(tabs, vibrantLightSwatch.getRgb());
            setStatusBarColor(activity, vibrantDarkSwatch.getRgb());
        }
    }

    /**
     * Changes the color of the status bar. This has no effect if invoked on a version of android
     * that is below API Level 21 (Lollipop)
     *
     * @param activity       The activity
     * @param statusBarColor The color to use
     */
    public static void setStatusBarColor(Activity activity, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
        }
    }

    /**
     * Helper method to change the indicator color of Material Tabs. This is implemented
     * using Java's reflection API because this method is private in the implementation of
     * TabLayout.
     *
     * @param tabs  The TabLayout which indicator's color will be changed
     * @param color The color to use
     */
    public static void setTabIndicatorColor(TabLayout tabs, int color) {
        try {
            Field tabStripField = TabLayout.class.getDeclaredField("mTabStrip");
            tabStripField.setAccessible(true);
            Class<?> c = Class.forName("android.support.design.widget.TabLayout$SlidingTabStrip");
            Method changeIndicatorColor = c.getDeclaredMethod("setSelectedIndicatorColor", int.class);
            changeIndicatorColor.setAccessible(true);
            Object object = tabStripField.get(tabs);
            changeIndicatorColor.invoke(object, color);
        } catch (NoSuchFieldException e) {
            Utils.log("NoSuchFieldException : " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Utils.log("NoSuchMethodException : " + e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Utils.log("InvocationTargetException : " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Utils.log("IllegalAccessException : " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Utils.log("ClassNotFoundException : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Changes the background color of the toolbar to the vibrant swatch which is
     * generated by the Palette class from the given bitmap.
     *
     * @param activity The activity
     * @param bitmap   The bitmap which to load colors from
     */
    public static void decorateToolbar(Activity activity, Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
        Utils.log("Vibrant swatch is null : " + (vibrantSwatch == null));

        if (vibrantSwatch != null) {
            int primaryColor = vibrantSwatch.getRgb();
            Toolbar toolbar = ((ToolbarGettable) activity).getToolbar();
            toolbar.setBackgroundColor(primaryColor);
        }
    }

    /**
     * Returns a rounded version of the given bitmap
     *
     * @param bitmap The bitmap that will be masked with a circle frame
     * @return The rounded bitmap
     */
    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * Capitalizes the first character of the string
     *
     * @param string The string that will be capitalized
     * @return The capitalized string
     */
    public static String capitalize(String string) {
        if (null != string && !string.isEmpty()) {
            return Character.toUpperCase(string.charAt(0)) + string.substring(1);
        }
        return string;
    }

    /**
     * Helper method to set the item click listener on the RecyclerView which is obviously not
     * available to the core RecyclerView class.
     *
     * @param recyclerView The object which the item click listener will be attached
     * @param listener The callbacks which will be invoked appropriately
     */
    public static void setOnItemClickListener(final RecyclerView recyclerView, final CustomClickListener listener) {
        final GestureDetector detector = new GestureDetector(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (null != view) {
                    int position = recyclerView.getChildLayoutPosition(view);
                    listener.onClick(view, position);
                    return true;
                }

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (null != view) {
                    int position = recyclerView.getChildLayoutPosition(view);
                    listener.onLongClick(view, position);
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return detector.onTouchEvent(e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * Wrapper to start the RecipeActivity with extras which is put in the Intent object.
     * Extras include the recipe id and name
     *
     * @param activity The activity that requested this action
     * @param id The recipe id
     * @param recipeName The recipe name
     */
    public static void startRecipeActivity(Activity activity, int id, String recipeName) {
        Intent intent = new Intent(activity, RecipeActivity.class);
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_PK, id);
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_NAME, recipeName);
        activity.startActivity(intent);
    }

    /**
     * Wrapper to start the IngredientActivity with extras which is put in the Intent object.
     * Extras include the ingredient id and name
     *
     * @param activity The activity that requested this action
     * @param id The ingredient id
     * @param ingredientName The ingredient name
     */
    public static void startIngredientActivity(Activity activity, int id, String ingredientName) {
        Intent intent = new Intent(activity, IngredientActivity.class);
        intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_PK, id);
        intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_NAME, ingredientName);
        activity.startActivity(intent);
    }

    /**
     * The interface that is used in the setOnItemClickListener function above.
     */
    public interface CustomClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    /**
     * Convenience class that implements the CustomClickListener with blank implementations.
     */
    public static class SimpleClickListener implements CustomClickListener {

        @Override
        public void onClick(View view, int position) {

        }

        @Override
        public void onLongClick(View view, int position) {

        }
    }

}
