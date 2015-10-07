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
    public static final int CONNECT_TIMEOUT = 0;
    public static final int READ_TIMEOUT = 0;
    public static final boolean DO_INPUT = true;

    /**
     * RecipeActivity
     */
    public static final String[] RECIPE_COMPONENTS = {
            "Summary",
            "Ingredients",
            "Steps"
    };

    /**
     * RecommendRecipesActivity
     */
    public static final String RECOMMEND_RECIPES_EXACT = "Recipes";
    public static final String RECOMMEND_RECIPES_NEARLY_THERE = "Nearly There";
    public static final String RECOMMEND_RECIPE_FRAGMENT_POSITION = "fragment_position";

}
