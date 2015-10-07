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

import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;

/**
 * The activity that displays all recipes in a list format
 */
public class RecipesActivity extends BaseActivity {

    private static final String TAG = "RecipesActivity";

    private boolean mIsSyncing = false;

    private RecipeTypesAdapter mAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private JSONArray mCachedRecipeTypes;
    private RecipeManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        setUpUI();

        mManager = RecipeManager.get(this);
        mCachedRecipeTypes = mManager.getCachedRecipeTypes();

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        synchronize(mCachedRecipeTypes);

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
            case R.id.action_search:
                Intent intent = new Intent(this, RecipeSearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_recipes;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsSyncing && Utils.hasInternet(this)) {
            mIsSyncing = true;
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
                    if (null != result && !result.equalsIgnoreCase(mCachedRecipeTypes.toString())) {
                        try {
                            Log.i(TAG, "Got updated data. Updating recipes activity");
                            JSONArray freshRecipeTypes = new JSONObject(result).optJSONArray(Api.RESULTS);
                            mManager.cacheRecipeTypes(freshRecipeTypes);
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
        if (mAdapter == null) {
            mAdapter = new RecipeTypesAdapter(getSupportFragmentManager(), cachedRecipeTypes);
        }

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
    }
}
