package team.jcandfriends.cookstogo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import team.jcandfriends.cookstogo.adapters.RecommendRecipesAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

public class RecommendedRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_recipes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setTitle("Results");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Utils.initializeImageLoader(this);

        VirtualBasketManager virtualBasketManager = VirtualBasketManager.get(this);
        final RecipeManager recipeManager = RecipeManager.get(this);

        recipeManager.recommendRecipes(virtualBasketManager.getAll(), new RecipeManager.Callbacks() {
            @Override
            public void onSuccess(final JSONObject result) {
                ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
                viewPager.setAdapter(new RecommendRecipesAdapter(getSupportFragmentManager(), result));

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                tabLayout.setupWithViewPager(viewPager);

                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure() {
                Toast.makeText(RecommendedRecipesActivity.this, "Something unexpected has occurred", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
