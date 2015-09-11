package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import team.jcandfriends.cookstogo.adapters.IngredientTypesAdapter;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.SearchManager;

/**
 * The activity that displays all ingredients
 */
public class IngredientsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        setUpUI();
        setDrawerSelectedItem(R.id.navigation_ingredients);

        IngredientManager ingredientManager = IngredientManager.get(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        IngredientTypesAdapter adapter = new IngredientTypesAdapter(getSupportFragmentManager(), ingredientManager.getCachedIngredientTypes());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra(SearchManager.EXTRA_SEARCH_WHAT, SearchManager.INGREDIENTS_SEARCH_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ingredients, menu);

        return true;
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_ingredients;
    }

}
