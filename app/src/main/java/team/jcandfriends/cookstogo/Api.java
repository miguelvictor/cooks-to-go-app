package team.jcandfriends.cookstogo;

/**
 * Class that contains all keys in the JSON string returned by the axasas.
 */
public final class Api {

    public static final String RECIPES = "http://cookstogo.herokuapp.com/api/recipes/";
    public static final String RECIPE_TYPES = "http://cookstogo.herokuapp.com/api/recipe-types/";
    public static final String INGREDIENTS = "http://cookstogo.herokuapp.com/api/ingredients/";
    public static final String INGREDIENT_TYPES = "http://cookstogo.herokuapp.com/api/ingredient-types/";

    /**
     * Relating to the Recipe Model
     */
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_DESCRIPTION = "description";

    /**
     * Relating to the RecipeType Model
     */
    public static final String RECIPETYPE_NAME = "name";
    public static final String RECIPETYPE_RECIPES = "recipes";
    public static final String RECIPETYPE_RESULTS = "results";

    /**
     * Relating to the Ingredient Model
     */
    public static final String INGREDIENT_NAME = "name";

    /**
     * Relating to the IngredientType Model
     */
    public static final String INGREDIENTTYPE_NAME = "name";
    public static final String INGREDIENTTYPE_INGREDIENTS = "ingredients";

}
