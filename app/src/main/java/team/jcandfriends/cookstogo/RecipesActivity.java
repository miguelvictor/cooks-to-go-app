package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;

public class RecipesActivity extends BaseActivity {

    private boolean isList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        setUpUI();
        setUpTabs();

        if (Utils.hasInternet(this)) {
            Utils.showSnackbar(this, "Grabbing yummy recipes ...");
            try {
                GrabJsonTask task = new GrabJsonTask();
                task.execute(new URL(Constants.URL_RECIPES_ALL));
                JSONObject obj = task.get();
                Data.initializeData(obj);
                Utils.showSnackbar(this, "Got the recipes XD");
            } catch (MalformedURLException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Utils.showSnackbar(this, "No connection");
        }
    }

    private void setUpTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        RecipeTypesAdapter adapter = new RecipeTypesAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipes, menu);

        MenuItem toggleViewItem = menu.findItem(R.id.action_toggle_view);
        boolean isList = Utils.getPersistedBoolean(this, Constants.VIEW_TYPE, true);

        if (!isList) {
            this.isList = false;
            toggleViewItem.setIcon(R.mipmap.ic_view_agenda);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, RecipeSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            case R.id.action_toggle_view:
                if (isList) {
                    isList = false;
                    item.setIcon(R.mipmap.ic_view_agenda);
                    Utils.persistBoolean(this, Constants.VIEW_TYPE, false);
                } else {
                    isList = true;
                    item.setIcon(R.mipmap.ic_view_quilt);
                    Utils.persistBoolean(this, Constants.VIEW_TYPE, true);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_recipes;
    }

}
