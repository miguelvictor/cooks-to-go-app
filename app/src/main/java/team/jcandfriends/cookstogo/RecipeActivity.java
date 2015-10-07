package team.jcandfriends.cookstogo;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import team.jcandfriends.cookstogo.adapters.RecipeComponentsAdapter;
import team.jcandfriends.cookstogo.inflector.English;
import team.jcandfriends.cookstogo.interfaces.TabsToolbarGettable;
import team.jcandfriends.cookstogo.managers.RecipeManager;
import team.jcandfriends.cookstogo.managers.RecipeManager.Callbacks;

/**
 * The activity that displays the details of a recipe.
 */
public final class RecipeActivity extends AppCompatActivity implements TabsToolbarGettable {

    public static final String EXTRA_RECIPE_PK = "extra_recipe_pk";
    public static final String EXTRA_RECIPE_NAME = "extra_recipe_name";

    private static final String TAG = "RecipeActivity";

    private Toolbar toolbar;
    private View coordinatorLayout;
    private TabLayout tabLayout;
    private RecipeComponentsAdapter adapter;
    private RecipeManager manager;

    private JSONObject recipe;
    private boolean isSyncing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent data = getIntent();
        int recipeId = data.getIntExtra(RecipeActivity.EXTRA_RECIPE_PK, -1);
        String recipeName = data.getStringExtra(EXTRA_RECIPE_NAME);

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(recipeName);

        manager = RecipeManager.get(this);
        recipe = manager.getCachedRecipe(recipeId);

        setupLayout(recipeId);
    }

    private void setupLayout(int recipeId) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new RecipeComponentsAdapter(getSupportFragmentManager(), recipeId);
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
    protected void onDestroy() {
        super.onDestroy();
        Utils.setStatusBarColor(this, Colors.PRIMARY_COLOR_DARK);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_rate_recipe:
                if (Utils.hasInternet(this)) {
                    showRateRecipe();
                } else {
                    Snackbar.make(coordinatorLayout, "No connection", Snackbar.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRateRecipe() {

        View layout = this.getLayoutInflater().inflate(R.layout.dialog_rate_recipe, null);
        final RatingBar ratingBar = (RatingBar) layout.findViewById(R.id.rating_bar);

        new Builder(this)
                .setTitle("Rate Recipe")
                .setView(layout)
                .setPositiveButton("Submit", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(RecipeActivity.this.coordinatorLayout, "Submitting rating...", Snackbar.LENGTH_SHORT).show();
                        RecipeManager.get(RecipeActivity.this).rate(RecipeActivity.this, RecipeActivity.this.recipe.optInt(Api.RECIPE_PK), (int) ratingBar.getRating(), new Callbacks() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                double newRating = data.optDouble(Api.RECIPE_RATING);
                                int reviewCount = data.optInt(Api.RECIPE_REVIEWS);
                                String formattedRating = String.format("Rating: %.2f stars (%d %s)", newRating, reviewCount, English.plural("review", reviewCount));
                                ((TextView) findViewById(R.id.recipe_rating)).setText(formattedRating);
                                Snackbar.make(RecipeActivity.this.coordinatorLayout, "Your rating has been saved.", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Snackbar.make(RecipeActivity.this.coordinatorLayout, "Failed to submit your rating.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isSyncing && Utils.hasInternet(this)) {
            isSyncing = false;
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        URL url = new URL(Api.RECIPES + recipe.optInt(Api.RECIPE_PK));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        if (connection.getResponseCode() == 200) {
                            InputStream is = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                            String line;
                            StringBuilder response = new StringBuilder();

                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            return response.toString();
                        }
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "Malformed URL", e);
                    } catch (IOException e) {
                        Log.e(TAG, "Cannot open connection", e);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);

                    if (null != result && !result.equalsIgnoreCase(recipe.toString())) {
                        try {
                            JSONObject freshRecipe = new JSONObject(result);
                            manager.cacheRecipe(freshRecipe);
                            synchronize(freshRecipe);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response as valid JSON", e);
                        }
                    }
                }
            }.execute();
        }
    }

    public void synchronize(JSONObject recipe) {
        this.recipe = recipe;
        adapter.notifyDataSetChanged();
    }
}
