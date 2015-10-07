package team.jcandfriends.cookstogo.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Utils;

public class VirtualBasketManager {

    /**
     * Intent Actions
     */
    public static final String ADD_NEW_VIRTUAL_BASKET = "add_new_virtual_basket";
    public static final String ADD_INGREDIENT_TO_VIRTUAL_BASKET = "add_ingredient_to_virtual_basket";

    /**
     * Constants related to a virtual basket object
     */
    public static final String VIRTUAL_BASKET_NAME = "name";

    public static final String VIRTUAL_BASKET_ITEMS = "items";
    private static final String PERSISTENT_VIRTUAL_BASKETS = "persistent_virtual_baskets";

    private static final String TAG = "VirtualBasketManager";

    private static VirtualBasketManager SOLE_INSTANCE;

    private SharedPreferences preferences;
    private ArrayList<JSONObject> mVirtualBaskets;

    private VirtualBasketManager(Context context) {
        this.preferences = context.getSharedPreferences(PERSISTENT_VIRTUAL_BASKETS, Context.MODE_PRIVATE);
        String virtualBasketsAsString = preferences.getString(PERSISTENT_VIRTUAL_BASKETS, null);

        if (virtualBasketsAsString != null) {
            try {
                mVirtualBaskets = Utils.jsonArrayToList(new JSONArray(virtualBasketsAsString));
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json as valid JSONArray of virtual baskets", e);
            }
        } else {
            mVirtualBaskets = new ArrayList<>();
        }
    }

    public static VirtualBasketManager get(Context context) {
        if (SOLE_INSTANCE == null) {
            SOLE_INSTANCE = new VirtualBasketManager(context);
        }

        return SOLE_INSTANCE;
    }

    /**
     * Returns a list of all virtual baskets
     *
     * @return
     */
    public ArrayList<JSONObject> getAll() {
        return mVirtualBaskets;
    }

    public ArrayList<String> getAllAsString() {
        ArrayList<String> names = new ArrayList<>();

        for (JSONObject virtualBasket : mVirtualBaskets) {
            names.add(virtualBasket.optString(VIRTUAL_BASKET_NAME));
        }

        return names;
    }

    public JSONObject get(int position) {
        return mVirtualBaskets.get(position);
    }

    /**
     * Adds the ingredient to the virtual basket on the given positon
     *
     * @param position   the position of the virtual basket on the JSONArray
     * @param ingredient the ingredient that will be added to the items of the virtual basket
     */
    public void addTo(int position, JSONObject ingredient) {
        try {
            JSONObject virtualBasket = mVirtualBaskets.get(position);
            JSONArray items = virtualBasket.optJSONArray(VIRTUAL_BASKET_ITEMS);
            items.put(ingredient);
            virtualBasket.put(VIRTUAL_BASKET_ITEMS, items);
            persist();
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Attempt to add an ingredient to a virtual basket that doesn't exist", e);
        } catch (JSONException e) {
            Log.e(TAG, "Attempt to put items to the virtual basket failed", e);
        }
    }

    public ArrayList<JSONObject> getItems(JSONObject virtualBasket) {
        return Utils.jsonArrayToList(virtualBasket.optJSONArray(VIRTUAL_BASKET_ITEMS));
    }

    public int getCount() {
        return mVirtualBaskets.size();
    }

    public void add(String virtualBasketName) {
        JSONObject virtualBasket = new JSONObject();
        try {
            virtualBasket.put(VIRTUAL_BASKET_NAME, virtualBasketName);
            virtualBasket.put(VIRTUAL_BASKET_ITEMS, new JSONArray());
        } catch (JSONException e) {
            Log.e(TAG, "Error putting virtual basket name", e);
        }

        mVirtualBaskets.add(virtualBasket);
        persist();
    }

    public void remove(JSONObject virtualBasket) {
        mVirtualBaskets.remove(virtualBasket);
        persist();
    }

    private void persist() {
        preferences.edit().putString(PERSISTENT_VIRTUAL_BASKETS, Utils.listToJsonArray(mVirtualBaskets).toString()).apply();
    }

    public boolean isAlreadyAdded(String virtualBasketName) {
        for (JSONObject i : mVirtualBaskets) {
            if (virtualBasketName.equalsIgnoreCase(i.optString(VIRTUAL_BASKET_NAME))) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlreadyAddedTo(int position, JSONObject ingredient) {
        JSONObject virtualBasket = mVirtualBaskets.get(position);
        JSONArray items = virtualBasket.optJSONArray(VIRTUAL_BASKET_ITEMS);

        JSONObject currentItem;
        final int toCompare = ingredient.optInt(Api.INGREDIENT_PK);
        for (int i = 0; i < items.length(); i++) {
            currentItem = items.optJSONObject(i);
            if (toCompare == currentItem.optInt(Api.INGREDIENT_PK)) {
                return true;
            }
        }

        return false;
    }

    public void deleteAll() {
        mVirtualBaskets.clear();
        preferences.edit().remove(PERSISTENT_VIRTUAL_BASKETS).apply();
    }

    public void delete(JSONObject virtualBasket) {
        int virtualBasketIndex = indexOf(virtualBasket);
        if (virtualBasketIndex != -1) {
            mVirtualBaskets.remove(virtualBasketIndex);
            persist();
        }
    }

    private int indexOf(JSONObject virtualBasket) {
        String name = virtualBasket.optString(VIRTUAL_BASKET_NAME);

        JSONObject each;
        String eachName;
        for (int i = 0; i < mVirtualBaskets.size(); i++) {
            each = mVirtualBaskets.get(i);

            eachName = each.optString(VIRTUAL_BASKET_NAME);
            if (name.equals(eachName)) {
                return i;
            }
        }

        return -1;
    }

    public void deleteFrom(int virtualBasketPosition, int ingredientPosition) {
        try {
            JSONObject virtualBasket = mVirtualBaskets.get(virtualBasketPosition);
            ArrayList<JSONObject> ingredients = Utils.jsonArrayToList(virtualBasket.optJSONArray(VIRTUAL_BASKET_ITEMS));
            ingredients.remove(ingredientPosition);
            virtualBasket.put(VIRTUAL_BASKET_ITEMS, Utils.listToJsonArray(ingredients));
        } catch (JSONException e) {
            Log.e(TAG, "Error while deleting ingredient(" + ingredientPosition + ") from virtualBasket(" + virtualBasketPosition + ")", e);
        }
    }
}