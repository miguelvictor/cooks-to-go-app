package team.jcandfriends.cookstogo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Data {

    private static Database database = null;

    public static void initializeRecipeTypes(Context context, String json) {
        ensureDatabase(context);
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray types = obj.getJSONArray("results");

            for (int i = 0; i < types.length(); i++) {
                obj = types.getJSONObject(i);
            }
        } catch (JSONException e) {
            Log.d(Constants.APP_DEBUG, "Exception occurred while initializing recipe types.");
        }
    }

    public static void initializeIngredientTypes(Context context, String json) {
        ensureDatabase(context);
    }

    private static void ensureDatabase(Context context) {
        if (database == null) {
            database = new Database(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
        }
    }

    private static class Database extends SQLiteOpenHelper {

        public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            db.execSQL(DatabaseConstants.RECIPE_TYPE_CREATE);
            db.execSQL(DatabaseConstants.INGREDIENT_TYPE_CREATE);
            db.endTransaction();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.RECIPE_TYPE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.INGREDIENT_TYPE_TABLE);
            db.endTransaction();
        }
    }
}
