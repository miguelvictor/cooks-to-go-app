package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Class used for each activity used in the NavigationView. This abstract class has all the boilerplate
 * code in implementing the navigation drawer which switches activities instead of fragments.
 */
public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;
    protected NavigationView navigationView;

    /**
     * Sets up the DrawerLayout, Toolbar, and NavigationView
     *
     * @return the Toolbar
     */
    protected Toolbar setUpUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // TODO : comment this in production
        if (null == toolbar) {
            throw new RuntimeException("Extending BaseActivity without attaching a Toolbar.");
        }

        // TODO : comment this in production
        if (null == drawerLayout) {
            throw new RuntimeException("Extending BaseActivity without attaching a DrawerLayout.");
        }

        // TODO : comment this in production
        if (null == navigationView) {
            throw new RuntimeException("Extending BaseActivity without attaching a NavigationView.");
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // setup drawer items clicks
        navigationView.setNavigationItemSelectedListener(this);

        drawerToggle.syncState();

        return toolbar;
    }

    /**
     * Tints the text and icon of this menu item.
     *
     * @param menuItemId the id of the menu that will be tinted
     */
    public void setDrawerSelectedItem(int menuItemId) {
        MenuItem item = navigationView.getMenu().findItem(menuItemId);
        if (item != null) {
            item.setChecked(true);
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent intent = null;
        boolean shouldFinish = true;

        if (shouldPerformNavigationClick(menuItem)) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_recipes:
                    intent = new Intent(BaseActivity.this, RecipesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
                case R.id.navigation_ingredients:
                    intent = new Intent(BaseActivity.this, IngredientsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
                case R.id.navigation_virtual_basket:
                    intent = new Intent(BaseActivity.this, VirtualBasketActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
                case R.id.navigation_settings:
                    intent = new Intent(BaseActivity.this, SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;

                case R.id.navigation_help:
                    intent = new Intent(BaseActivity.this, HelpActivity.class);
                    shouldFinish = false;
                    break;
                case R.id.navigation_feedback:
                    intent = new Intent(BaseActivity.this, FeedbackActivity.class);
                    shouldFinish = false;
                    break;
            }
        }

        drawerLayout.closeDrawers();

        if (intent != null) {
            startActivity(intent);

            if (shouldFinish) {
                finish();
                overridePendingTransition(0, 0);
            }
        }

        return false;
    }

    public abstract boolean shouldPerformNavigationClick(MenuItem menuItem);

}
