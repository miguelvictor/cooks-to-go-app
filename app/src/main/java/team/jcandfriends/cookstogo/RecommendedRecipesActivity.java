package team.jcandfriends.cookstogo;

import android.R.id;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.adapters.RecommendRecipesAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;
import team.jcandfriends.cookstogo.managers.RecipeManager.Callbacks;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

public class RecommendedRecipesActivity extends AppCompatActivity {

    private static final String TAG = "RecommendedRecipesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_recommended_recipes);

        try {
            JSONObject virtualBasket = new JSONObject(getIntent().getStringExtra(VirtualBasketsActivity.VIRTUAL_BASKET_EXTRA));
            Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
            this.setSupportActionBar(toolbar);

            ActionBar actionBar = this.getSupportActionBar();
            assert actionBar != null;

            actionBar.setTitle("Results");
            actionBar.setDisplayHomeAsUpEnabled(true);

            Utils.initializeImageLoader(this);

            VirtualBasketManager virtualBasketManager = VirtualBasketManager.get(this);
            RecipeManager recipeManager = RecipeManager.get(this);

            recipeManager.recommendRecipes(virtualBasketManager.getItems(virtualBasket), new Callbacks() {
                @Override
                public void onSuccess(JSONObject result) {
                    ViewPager viewPager = (ViewPager) RecommendedRecipesActivity.this.findViewById(R.id.view_pager);
                    viewPager.setAdapter(new RecommendRecipesAdapter(RecommendedRecipesActivity.this.getSupportFragmentManager(), result));

                    TabLayout tabLayout = (TabLayout) RecommendedRecipesActivity.this.findViewById(R.id.tab_layout);
                    tabLayout.setupWithViewPager(viewPager);

                    RecommendedRecipesActivity.this.findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(RecommendedRecipesActivity.this, "Something unexpected has occurred", Toast.LENGTH_SHORT).show();
                    RecommendedRecipesActivity.this.finish();
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Exception while instantiating virtual basket JSONObject", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.home:
                this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
