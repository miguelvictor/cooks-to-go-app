package team.jcandfriends.cookstogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.fragments.IngredientTypeFragment;

public class IngredientTypesAdapter extends FragmentPagerAdapter {

    public IngredientTypesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new Fragment();
    }

    @Override
    public int getCount() {
        return Constants.INGREDIENT_TYPES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.INGREDIENT_TYPES[position];
    }
}
