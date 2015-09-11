package team.jcandfriends.cookstogo;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Manager of the items in the virtual basket.
 */
public class VirtualBasketContainer {

    public static final String VIRTUAL_BASKET_ITEMS = "virtual_basket_items";
    public static final String VIRTUAL_INGREDIENT = "ingredient";

    private static ArrayList<JSONObject> data;

    public static void initialize(Context context) {
        JSONArray cachedItems = Utils.getPersistedJSONArray(context, VIRTUAL_BASKET_ITEMS);

        if (null == cachedItems) {
            data = new ArrayList<>();
        } else {
            data = Utils.jsonArrayToList(cachedItems);
        }
    }

    public static void addItem(JSONObject object) {
        if (null == data) {
            throw new RuntimeException("Attempted to add JSONObject when static data is null.");
        }

        data.add(object);
    }

    public static void removeItem(JSONObject object) {
        if (null == data) {
            throw new RuntimeException("Attempted to add JSONObject when static data is null.");
        }

        data.remove(object);
    }

    public static boolean isAlreadyAdded(Activity activity, JSONObject object) {
        if (null == data) {
            JSONArray cachedItems = Utils.getPersistedJSONArray(activity, VIRTUAL_BASKET_ITEMS);
            if (null == cachedItems) {
                data = new ArrayList<>();
            } else {
                data = Utils.jsonArrayToList(cachedItems);
            }
        }

        return data.contains(object);
    }

    public static ArrayList<JSONObject> getData() {
        return data;
    }

    public static void persist(Context context) {
        JSONArray array = Utils.listToJsonArray(data);
        Utils.persistJSONArray(context, VIRTUAL_BASKET_ITEMS, array);
    }

    public static void deleteAll(Context context) {
        Utils.persistJSONArray(context, VIRTUAL_BASKET_ITEMS, null);
    }

}