package team.jcandfriends.cookstogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.fragments.RecipeTypeFragment;

/**
 * RecipeTypesAdapter lays out all the recipe types fragment which also contains the recipes
 * of that type. This class is responsible in making the fragments which is used by the TabLayout.
 * <p/>
 * Subordinates: RecipeTypeFragment
 */
public class RecipeTypesAdapter extends FragmentStatePagerAdapter {

    private JSONArray recipeTypes;

    public RecipeTypesAdapter(FragmentManager fm, JSONArray recipeTypes) {
        super(fm);
        this.recipeTypes = recipeTypes;
    }

    @Override
    public Fragment getItem(int position) {
        JSONArray recipes = recipeTypes.optJSONObject(position).optJSONArray(Api.RECIPE_TYPE_RECIPES);
        return RecipeTypeFragment.newInstance(recipes);
    }

    @Override
    public int getCount() {
        return recipeTypes.length();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return recipeTypes.optJSONObject(position).optString(Api.RECIPE_TYPE_NAME);
    }
}