package team.jcandfriends.cookstogo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.adapters.RecipeComponentsAdapter;
import team.jcandfriends.cookstogo.interfaces.TabsToolbarGettable;

public final class RecipeActivity extends AppCompatActivity implements TabsToolbarGettable {

    public static final String EXTRA_RECIPE_PK = "extra_recipe_pk";
    public static final String EXTRA_RECIPE_NAME = "extra_recipe_name";

    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_PK, -1);
        setupLayout(recipeId);

        try {
            JSONObject recipe = Data.getCachedRecipe(this, recipeId);
            actionBar.setTitle(recipe.optString(Api.RECIPE_NAME));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupLayout(int recipeId) {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final RecipeComponentsAdapter adapter = new RecipeComponentsAdapter(getSupportFragmentManager(), recipeId);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public TabLayout getTabLayout() {
        return tabLayout;
    }
}
