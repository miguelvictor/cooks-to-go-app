package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import team.jcandfriends.cookstogo.R.anim;
import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;

/**
 * The activity that displays all recipes in a list format
 */
public class RecipesActivity extends BaseActivity {

    private static final String TAG = "RecipesActivity";

    private boolean isSyncing = false;

    private RecipeTypesAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private JSONArray cachedRecipeTypes;
    private RecipeManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_recipes);
        setUpUI();

        manager = RecipeManager.get(this);
        cachedRecipeTypes = manager.getCachedRecipeTypes();

        viewPager = (ViewPager) findViewById(id.view_pager);
        tabLayout = (TabLayout) findViewById(id.tab_layout);
        synchronize(cachedRecipeTypes);

        Utils.initializeImageLoader(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.action_search:
                Intent intent = new Intent(this, RecipeSearchActivity.class);
                startActivity(intent);
                overridePendingTransition(anim.abc_fade_in, anim.abc_fade_out);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != id.navigation_recipes;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isSyncing && Utils.hasInternet(this)) {
            isSyncing = true;
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        URL url = new URL(Api.RECIPE_TYPES);
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
                    if (null != result && !result.equalsIgnoreCase(cachedRecipeTypes.toString())) {
                        try {
                            Log.i(TAG, "Got updated data. Updating recipes activity");
                            JSONArray freshRecipeTypes = new JSONObject(result).optJSONArray(Api.RESULTS);
                            manager.cacheRecipeTypes(freshRecipeTypes);
                            synchronize(freshRecipeTypes);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response as valid JSON", e);
                        }
                    }
                }
            }.execute();
        }
    }

    private void synchronize(JSONArray cachedRecipeTypes) {
        if (adapter == null) {
            adapter = new RecipeTypesAdapter(getSupportFragmentManager(), cachedRecipeTypes);
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }
}
