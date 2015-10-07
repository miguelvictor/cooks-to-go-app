package team.jcandfriends.cookstogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Swatch;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.interfaces.TabsToolbarGettable;
import team.jcandfriends.cookstogo.interfaces.ToolbarGettable;

/**
 * Class that contains all utility methods for common tasks.
 */
public final class Utils {

    private static final String TAG = "Utils";

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
                .make(activity.findViewById(id.coordinator_layout), message, Snackbar.LENGTH_SHORT)
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
        Swatch vibrant = p.getVibrantSwatch();
        Swatch lightVibrant = p.getLightVibrantSwatch();
        Swatch darkVibrant = p.getDarkVibrantSwatch();

        if (vibrant != null && lightVibrant != null && darkVibrant != null) {
            int primaryColor = vibrant.getRgb();
            Toolbar toolbar = ((TabsToolbarGettable) activity).getToolbar();
            toolbar.setBackgroundColor(primaryColor);
            TabLayout tabs = ((TabsToolbarGettable) activity).getTabLayout();
            tabs.setBackgroundColor(primaryColor);
            Utils.setTabIndicatorColor(tabs, lightVibrant.getRgb());
            Utils.setStatusBarColor(activity, darkVibrant.getRgb());
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
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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
            Log.e(TAG, "NoSuchFieldException", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "ClassNotFoundException", e);
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
        Swatch vibrantSwatch = p.getVibrantSwatch();
        log("Vibrant swatch is null : " + (vibrantSwatch == null));

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
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
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
    public static void setOnItemClickListener(final RecyclerView recyclerView, final Utils.CustomClickListener listener) {
        final GestureDetector detector = new GestureDetector(recyclerView.getContext(), new SimpleOnGestureListener() {
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

        recyclerView.addOnItemTouchListener(new OnItemTouchListener() {
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

    public static void initializeImageLoader (Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

            ImageLoaderConfiguration config = new Builder(context)
                    .defaultDisplayImageOptions(defaultOptions)
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }

    public static ArrayList<String> filter(ArrayList<String> collection, Utils.FilterPredicate predicate) {
        ArrayList<String> filteredList = new ArrayList<>();

        for (String next : collection) {
            if (predicate.evaluate(next)) {
                filteredList.add(next);
            }
        }

        return filteredList;
    }

    public static ArrayList<String> toStringList (JSONArray array) {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            list.add(array.optString(i));
        }

        return list;
    }

    public static JSONArray listToJsonArray(ArrayList<JSONObject> list) {
        JSONArray array = new JSONArray();

        for (JSONObject item : list) {
            array.put(item);
        }

        return array;
    }

    public static JSONArray stringListToJsonArray(ArrayList<String> list) {
        JSONArray array = new JSONArray();

        for (String item : list) {
            array.put(item);
        }

        return array;
    }

    public static ArrayList<JSONObject> jsonArrayToList(JSONArray array) {
        ArrayList<JSONObject> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            list.add(array.optJSONObject(i));
        }

        return list;
    }

    /**
     * Reference: http://english.stackexchange.com/questions/69162/are-these-plural-or-singular
     *
     * @param x the floating-point number in string format
     * @return the fraction version
     */
    public static String fractionize(String x) {
        String tokens[] = x.split("\\.");
        String whole = tokens[0].equals("0") ? "" : tokens[0];
        String decimal = tokens[1];

        switch (decimal) {
            case "25":  // 1/4
                return whole + "¼";
            case "5":  // 1/2
                return whole + "½";
            case "75":  // 3/4
                return whole + "¾";
            case "3333":  // 1/3
                return whole + "⅓";
            case "6666":  // 2/3
                return whole + "⅔";
            case "2":  // 1/5
                return whole + "⅕";
            case "4":  // 2/5
                return whole + "⅖";
            case "6":  // 3/5
                return whole + "⅗";
            case "8":  // 4/5
                return whole + "⅘";
            case "1666":  // 1/6
                return whole + "⅙";
            case "8333":  // 5/6
                return whole + "⅚";
            case "125":  // 1/8
                return whole + "⅛";
            case "375":  // 3/8
                return whole + "⅜";
            case "625":  // 5/8
                return whole + "⅝";
            case "875":  // 7/8
                return whole + "⅞";
            default:
                return x;
        }
    }

    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getMacAddress();
    }

    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();

        if (view != null) {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void closeKeyboard(Activity activity, View view) {
        if (null != view) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * The interface that is used in the setOnItemClickListener function above.
     */
    public interface CustomClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public interface FilterPredicate {
        boolean evaluate (String object);
    }

    /**
     * Convenience class that implements the CustomClickListener with blank implementations.
     */
    public static class SimpleClickListener implements Utils.CustomClickListener {

        @Override
        public void onClick(View view, int position) {

        }

        @Override
        public void onLongClick(View view, int position) {

        }
    }

}
