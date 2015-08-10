package team.jcandfriends.cookstogo;

public final class DatabaseConstants {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "cookstogo";

    public static final String RECIPE_TYPE_TABLE = "recipe_type_table";
    public static final String INGREDIENT_TYPE_TABLE = "ingredient_type_table";

    public static final String RECIPE_TYPE_CREATE = "CREATE TABLE " + RECIPE_TYPE_TABLE + " (" +
            "name varchar(255)," +
            "picture varchar(255)" +
            ");";

    public static final String INGREDIENT_TYPE_CREATE = "CREATE TABLE " + INGREDIENT_TYPE_TABLE + " (" +
            "name varchar(255)," +
            "picture varchar(255)" +
            ");";

}
