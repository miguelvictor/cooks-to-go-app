package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;
import team.jcandfriends.cookstogo.managers.SearchManager;

/**
 * The activity that displays all recipes in a list format
 */
public class RecipesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        setUpUI();

        RecipeManager recipeManager = RecipeManager.get(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        RecipeTypesAdapter adapter = new RecipeTypesAdapter(getSupportFragmentManager(), recipeManager.getCachedRecipeTypes());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);

        Utils.initializeImageLoader(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipes, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra(SearchManager.EXTRA_SEARCH_WHAT, SearchManager.RECIPES_SEARCH_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_recipes;
    }

}
