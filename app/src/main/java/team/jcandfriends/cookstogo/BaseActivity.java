package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
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
public abstract class BaseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected NavigationView mNavigationView;

    /**
     * Sets up the DrawerLayout, Toolbar, and NavigationView
     *
     * @return the Toolbar
     */
    protected Toolbar setUpUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        // TODO : comment this in production
        if (null == toolbar) {
            throw new RuntimeException("Extending BaseActivity without attaching a Toolbar.");
        }

        // TODO : comment this in production
        if (null == mDrawerLayout) {
            throw new RuntimeException("Extending BaseActivity without attaching a DrawerLayout.");
        }

        // TODO : comment this in production
        if (null == mNavigationView) {
            throw new RuntimeException("Extending BaseActivity without attaching a NavigationView.");
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerToggle.syncState();

        return toolbar;
    }

    /**
     * Tints the mText and icon of this menu item.
     *
     * @param menuItemId the id of the menu that will be tinted
     */
    public void setDrawerSelectedItem(int menuItemId) {
        mNavigationView.getMenu().findItem(menuItemId).setChecked(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent intent = null;

        if (this.shouldPerformNavigationClick(menuItem)) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_recipes:
                    intent = new Intent(this, RecipesActivity.class);
                    break;
                case R.id.navigation_ingredients:
                    intent = new Intent(this, IngredientsActivity.class);
                    break;
                case R.id.navigation_virtual_basket:
                    intent = new Intent(this, VirtualBasketsActivity.class);
                    break;
                case R.id.navigation_settings:
                    intent = new Intent(this, SettingsActivity.class);
                    break;
            }
        }

        mDrawerLayout.closeDrawers();

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }

        return false;
    }

    public abstract boolean shouldPerformNavigationClick(MenuItem menuItem);

}
