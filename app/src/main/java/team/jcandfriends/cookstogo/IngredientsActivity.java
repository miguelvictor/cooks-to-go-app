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
import team.jcandfriends.cookstogo.adapters.IngredientTypesAdapter;
import team.jcandfriends.cookstogo.managers.IngredientManager;

/**
 * The activity that displays all ingredients
 */
public class IngredientsActivity extends BaseActivity {

    private static final String TAG = "IngredientsActivity";

    private boolean isSyncing = false;

    private JSONArray cachedIngredientTypes;
    private IngredientManager manager;

    private IngredientTypesAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_ingredients);
        setUpUI();
        setDrawerSelectedItem(id.navigation_ingredients);

        manager = IngredientManager.get(this);
        cachedIngredientTypes = manager.getCachedIngredientTypes();

        viewPager = (ViewPager) findViewById(id.view_pager);
        tabLayout = (TabLayout) findViewById(id.tab_layout);

        Log.i(TAG, "Initializing UI with cached ingredient types");
        synchronize(cachedIngredientTypes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.action_search:
                Intent intent = new Intent(this, IngredientSearchActivity.class);
                startActivity(intent);
                overridePendingTransition(anim.abc_fade_in, anim.abc_fade_out);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingredients, menu);
        return true;
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != id.navigation_ingredients;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isSyncing && Utils.hasInternet(this)) {
            Log.i(TAG, "Starting to fetch latest ingredient types");
            isSyncing = true;
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        URL url = new URL(Api.INGREDIENT_TYPES);
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

                    if (null != result && !result.equalsIgnoreCase(cachedIngredientTypes.toString())) {
                        try {
                            Log.i(TAG, "Got updated data. Replacing ingredients with fresh data.");
                            JSONArray freshIngredientTypes = new JSONObject(result).optJSONArray(Api.RESULTS);
                            manager.cacheIngredientTypes(freshIngredientTypes);
                            synchronize(freshIngredientTypes);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response as valid JSON", e);
                        }
                    } else {
                        Log.i(TAG, "Fresh ingredient types object is either null or it's just the same with the cached one.");
                    }
                }
            }.execute();
        }
    }

    private void synchronize(JSONArray cachedIngredientTypes) {
        if (adapter == null) {
            adapter = new IngredientTypesAdapter(getSupportFragmentManager(), cachedIngredientTypes);
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }
}
