package team.jcandfriends.cookstogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.fragments.RecipeComponentFragment;

public class RecipeComponentsAdapter extends FragmentPagerAdapter {

    public RecipeComponentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new RecipeComponentFragment();
    }

    @Override
    public int getCount() {
        return Constants.RECIPE_COMPONENTS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.RECIPE_COMPONENTS[position];
    }
}
