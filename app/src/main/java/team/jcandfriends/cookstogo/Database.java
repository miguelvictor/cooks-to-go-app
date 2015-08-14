package team.jcandfriends.cookstogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class Database {

    public static final int DB_VERSION = 1;

    public static final String DB_NAME = "CooksToGo";
    public static final String RECIPE_SEARCH_HISTORY = "recipe_search_history";
    public static final String INGREDIENT_SEARCH_HISTORY = "ingredient_search_history";

    private static SQLiteDatabase db;

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

    public static class RecipeSearchHistory {

        private static int getNextId(String table) {
            if (null == db)
                throw new RuntimeException("Call to getNextId() when database is null");

            Cursor cursor = db.rawQuery("select max(id) from " + table, null);
            final int nextId = cursor.getInt(0) + 1;
            cursor.close();
            // todo : make this work even if no records exist
            return nextId;
        }

        public static void add(Context context, String query) {
            if (db == null || !db.isOpen()) {
                db = new DatabaseOpenHelper(context).getWritableDatabase();
            }

            ContentValues cv = new ContentValues();
            // todo : insert the 'query' text
        }

    }

}
