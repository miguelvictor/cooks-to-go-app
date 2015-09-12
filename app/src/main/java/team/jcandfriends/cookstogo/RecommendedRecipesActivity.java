package team.jcandfriends.cookstogo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.adapters.RecipeAdapter;
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

        actionBar.setTitle("Recipe Results");
        actionBar.setDisplayHomeAsUpEnabled(true);

        VirtualBasketManager virtualBasketManager = VirtualBasketManager.get(this);
        final RecipeManager recipeManager = RecipeManager.get(this);

        recipeManager.recommendRecipes(virtualBasketManager.getAll(), new RecipeManager.Callbacks() {
            @Override
            public void onSuccess(final JSONObject result) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                if (result.optInt(Api.COUNT, 0) == 0) {
                    findViewById(R.id.no_results_found).setVisibility(View.VISIBLE);
                } else {
                    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                    final JSONArray results = result.optJSONArray(Api.RESULTS);

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setClickable(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(RecommendedRecipesActivity.this));
                    recyclerView.setAdapter(new RecipeAdapter(results));
                    recyclerView.setVisibility(View.VISIBLE);
                    Utils.setOnItemClickListener(recyclerView, new Utils.SimpleClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            JSONObject recipe = results.optJSONObject(position);
                            recipeManager.cacheRecipe(recipe);
                            Utils.startRecipeActivity(RecommendedRecipesActivity.this, recipe.optInt(Api.RECIPE_PK), recipe.optString(Api.RECIPE_NAME));
                        }
                    });
                }
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
