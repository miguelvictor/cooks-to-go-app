package team.jcandfriends.cookstogo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import team.jcandfriends.cookstogo.adapters.RecipeComponentsAdapter;

public final class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        final int recipeId = intent.getIntExtra(Constants.EXTRA_RECIPE_ID, -1);

        if (Data.hasCachedRecipe(this, recipeId)) {
            try {
                JSONObject recipe = Data.getCachedRecipe(this, recipeId);
                actionBar.setTitle(recipe.optString(Api.RECIPE_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                FetchRecipeTask task = new FetchRecipeTask();
                task.execute(recipeId);
                JSONObject recipe = task.get();
                actionBar.setTitle(recipe.optString(Api.RECIPE_NAME));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final RecipeComponentsAdapter adapter = new RecipeComponentsAdapter(getSupportFragmentManager(), recipeId);
        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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

    private class FetchRecipeTask extends AsyncTask<Integer, Void, JSONObject> {

        private AlertDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new AlertDialog.Builder(RecipeActivity.this)
                    .setTitle("Loading Recipe")
                    .setMessage("Please wait while loading recipe ...")
                    .create();
            progress.show();
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            try {
                JSONGrabber recipeGrabber = new JSONGrabber(Api.getRecipeUrl(params[0]));
                return recipeGrabber.grab();
            } catch (IOException | JSONException e) {
                Utils.log(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject recipe) {
            super.onPostExecute(recipe);
            if (null != recipe) {
                Data.cacheRecipe(RecipeActivity.this, recipe);
            }
            progress.hide();
        }
    }
}
