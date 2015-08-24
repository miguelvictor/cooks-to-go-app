package team.jcandfriends.cookstogo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.json.JSONArray;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.fragments.IngredientTypeFragment;

public class IngredientTypesAdapter extends FragmentPagerAdapter {

    private JSONArray ingredientTypes;

    public IngredientTypesAdapter(Context context, FragmentManager fm) {
        super(fm);
        ingredientTypes = Data.getIngredientTypes(context);
    }

    @Override
    public Fragment getItem(int position) {
        JSONArray ingredients = ingredientTypes.optJSONObject(position).optJSONArray(Api.INGREDIENT_TYPE_INGREDIENTS);
        return IngredientTypeFragment.newInstance(ingredients);
    }

    @Override
    public int getCount() {
        return ingredientTypes.length();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ingredientTypes.optJSONObject(position).optString(Api.INGREDIENT_TYPE_NAME);
    }
}
