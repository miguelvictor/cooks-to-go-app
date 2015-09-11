package team.jcandfriends.cookstogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.json.JSONArray;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.fragments.IngredientTypeFragment;

/**
 * IngredientTypesAdapter lays out all the ingredient types fragment which also contains the ingredients
 * of that type. This class is responsible in making the fragments which is used by the TabLayout.
 * <p/>
 * Subordinates: IngredientTypeFragment
 */
public class IngredientTypesAdapter extends FragmentPagerAdapter {

    private JSONArray ingredientTypes;

    public IngredientTypesAdapter(FragmentManager fm, JSONArray ingredientTypes) {
        super(fm);
        this.ingredientTypes = ingredientTypes;
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
