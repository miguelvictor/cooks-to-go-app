package team.jcandfriends.cookstogo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

// TODO : Remove (data == null) check on production

public class VirtualBasketContainer {

    public static final String VIRTUAL_BASKET_ITEMS = "virtual_basket_items";
    public static final String VIRTUAL_INGREDIENT = "ingredient";
    public static final String VIRTUAL_UNIT_OF_MEASURE = "unitOfMeasure";
    public static final String VIRTUAL_QUANTITY = "quantity";

    private static JSONArray data = null;

    public static void initialize(Context context) {
        data = Utils.getPersistedJSONArray(context, VIRTUAL_BASKET_ITEMS);
        if (null == data) {
            data = new JSONArray();
        }
    }

    public static void addItem(JSONObject object) {
        if (null == data) {
            throw new RuntimeException("Attempted to add JSONObject when static data is null.");
        }

        data.put(object);
    }

    public static void removeItem(JSONObject object) {
        if (null == data) {
            throw new RuntimeException("Attempted to add JSONObject when static data is null.");
        }
    }

    private static int getItemIndex(JSONObject object) {
        int length = data.length();
        JSONObject item;

        for (int i = 0; i < length; i++) {
            item = data.optJSONObject(i);
            if (item.equals(object)) {
                return i;
            }
        }
        return -1;
    }

    public static JSONArray getData() {
        return data;
    }

}

/**
 * [
 * {
 * "id": 1,
 * "quantity": 2,
 * "unitOfMeasure": "kilogram",
 * "unitOfMeasureId": 5,
 * "ingredient": "meat",
 * "ingredientId": 7
 * }
 * hannah n
 * ]
 **/