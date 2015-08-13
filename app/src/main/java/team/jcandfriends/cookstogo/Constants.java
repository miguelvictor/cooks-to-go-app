package team.jcandfriends.cookstogo;

public final class Constants {

    /**
     * Logging
     */
    public static final String APP_DEBUG = "APP_DEBUG";

    /**
     * Fragments
     */
    public static final String RECIPES_IN_FRAGMENT = "recipes_in_fragment";
    public static final String INGREDIENTS_IN_FRAGMENT = "ingredients_in_fragment";

    /**
     * HttpUrlConnection constants
     */
    public static final String REQUEST_METHOD_GET = "GET";
    public static final int CONNECT_TIMEOUT = 15000;
    public static final int READ_TIMEOUT = 15000;
    public static final boolean DO_INPUT = true;

    /**
     * SharedPreferences Keys
     */
    public static final String VIEW_TYPE = "view_type";

    public static final String[] RECIPE_COMPONENTS = {
            "Summary",
            "Ingredients",
            "Method"
    };

}
