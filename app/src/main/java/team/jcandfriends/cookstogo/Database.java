package team.jcandfriends.cookstogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class Database {

    public static final int DB_VERSION = 1;

    public static final String DB_NAME = "CooksToGo";

    /**
     * Table Names
     */
    public static final String RECIPE_SEARCH_HISTORY = "recipe_search_history";
    public static final String INGREDIENT_SEARCH_HISTORY = "ingredient_search_history";

    /**
     * Table Fields
     */
    public static final int SEARCH_HISTORY_QUERY = 1;

    private static SQLiteDatabase db;

    public static void addRecipeSearchHistory(Context context, String query) {
        if (db == null || !db.isOpen()) {
            db = new DatabaseOpenHelper(context).getWritableDatabase();
        }

        ContentValues cv = new ContentValues();
        cv.put("query", query);
        db.insert(RECIPE_SEARCH_HISTORY, null, cv);
    }

    public static String[] allRecipeSearchHistory(Context context) {
        if (db == null || !db.isOpen()) {
            db = new DatabaseOpenHelper(context).getWritableDatabase();
        }

        Cursor cursor = db.rawQuery("select * from " + RECIPE_SEARCH_HISTORY, null);
        String results[] = new String[cursor.getCount()];

        for (int i = 0; i < results.length; i++) {
            cursor.move(i);
            results[i] = cursor.getString(SEARCH_HISTORY_QUERY);
        }

        cursor.close();

        return results;
    }

    private static final class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            db.execSQL("CREATE table " + RECIPE_SEARCH_HISTORY + " (" +
                    "id int primary key not null," +
                    "query varchar(255) not null);");
            db.execSQL("CREATE table " + INGREDIENT_SEARCH_HISTORY + " (" +
                    "id int primary key not null," +
                    "query varchar(255) not null);");
            db.endTransaction();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + RECIPE_SEARCH_HISTORY);
            db.execSQL("DROP TABLE IF EXISTS " + INGREDIENT_SEARCH_HISTORY);
            db.endTransaction();
        }
    }


}
