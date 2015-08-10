package team.jcandfriends.cookstogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.fragments.RecipeTypeFragment;

public class RecipeTypesAdapter extends FragmentPagerAdapter {

    public RecipeTypesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return RecipeTypeFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return Constants.RECIPE_TYPES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.RECIPE_TYPES[position];
    }
}