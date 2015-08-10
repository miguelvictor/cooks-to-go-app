package team.jcandfriends.cookstogo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Data {

    private static JSONObject data;

    public static void initializeData(JSONObject object) {
        data = object;
    }

    public static class recipes {

        public static JSONArray all() {
            try {
                return data.getJSONArray("results");
            } catch (JSONException e) {
                return null;
            }
        }

    }

}
