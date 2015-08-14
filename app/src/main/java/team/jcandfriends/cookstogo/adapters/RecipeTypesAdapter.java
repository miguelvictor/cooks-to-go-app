package team.jcandfriends.cookstogo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.json.JSONArray;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.fragments.RecipeTypeFragment;

public class RecipeTypesAdapter extends FragmentPagerAdapter {

    private JSONArray recipeTypes;

    public RecipeTypesAdapter(Context context, FragmentManager fm) {
        super(fm);
        recipeTypes = Data.getRecipeTypes(context);
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