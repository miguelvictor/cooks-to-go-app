package team.jcandfriends.cookstogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.fragments.RecipeIngredientsFragment;
import team.jcandfriends.cookstogo.fragments.RecipeStepsFragment;
import team.jcandfriends.cookstogo.fragments.RecipeSummaryFragment;

public class RecipeComponentsAdapter extends FragmentPagerAdapter {

    private int recipeId;

    public RecipeComponentsAdapter(FragmentManager fm, int recipeId) {
        super(fm);
        this.recipeId = recipeId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RecipeSummaryFragment.newInstance(position, recipeId);
            case 1:
                return RecipeIngredientsFragment.newInstance(position, recipeId);
            default:
                return RecipeStepsFragment.newInstance(position, recipeId);
        }
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
