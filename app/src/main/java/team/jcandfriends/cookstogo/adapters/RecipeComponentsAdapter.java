package team.jcandfriends.cookstogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.fragments.RecipeIngredientsFragment;
import team.jcandfriends.cookstogo.fragments.RecipeStepsFragment;
import team.jcandfriends.cookstogo.fragments.RecipeSummaryFragment;

/**
 * RecipeComponentsAdapter is responsible for displaying the different components of
 * a recipe: Summary(includes the picture of the recipe and its overview), Ingredients, and Steps
 * <p/>
 * Subordinates: RecipeSummaryFragment, RecipeIngredientsFragment, RecipeStepsFragment
 */
public class RecipeComponentsAdapter extends FragmentPagerAdapter {

    private final int recipeId;

    public RecipeComponentsAdapter(FragmentManager fm, int recipeId) {
        super(fm);
        this.recipeId = recipeId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RecipeSummaryFragment.newInstance(this.recipeId);
            case 1:
                return RecipeIngredientsFragment.newInstance(this.recipeId);
            default:
                return RecipeStepsFragment.newInstance(this.recipeId);
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
