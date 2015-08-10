package team.jcandfriends.cookstogo;

public final class Constants {

    /**
     * API URLs : Django Server
     */
    public static final String URL_RECIPES_ALL = "http://cookstogo.herokuapp.com/api/recipes/";

    /**
     * Logging
     */
    public static final String APP_DEBUG = "APP_DEBUG";

    /**
     * Fragments
     */
    public static final String FRAGMENT_POSITION = "FRAGMENT_POSITION";

    /**
     * Networking Stuff
     */
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    public static final int CONNECT_TIMEOUT = 15000;
    public static final int READ_TIMEOUT = 15000;

    /**
     * SharedPreferences Keys
     */
    public static final String VIEW_TYPE = "view_type";

    public static final String[] RECIPE_TYPES = {
            "All",
            "Appetizer",
            "Salad",
            "Main Dish",
            "Dessert",
            "Breakfast",
            "Lunch"
    };

    public static final String[] RECIPE_COMPONENTS = {
            "Summary",
            "Ingredients",
            "Method"
    };

    public static final String[] INGREDIENT_TYPES = {
            "Condiment",
            "Vegetable",
            "Fruit",
            "Meat",
            "Chicken",
            "Sweetener"
    };

}
