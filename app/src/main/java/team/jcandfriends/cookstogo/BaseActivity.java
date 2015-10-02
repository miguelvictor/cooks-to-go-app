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

import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.string;

/**
 * Class used for each activity used in the NavigationView. This abstract class has all the boilerplate
 * code in implementing the navigation drawer which switches activities instead of fragments.
 */
public abstract class BaseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;
    protected NavigationView navigationView;

    /**
     * Sets up the DrawerLayout, Toolbar, and NavigationView
     *
     * @return the Toolbar
     */
    protected Toolbar setUpUI() {
        Toolbar toolbar = (Toolbar) this.findViewById(id.toolbar);
        this.drawerLayout = (DrawerLayout) this.findViewById(id.drawer_layout);
        this.navigationView = (NavigationView) this.findViewById(id.navigation_view);

        // TODO : comment this in production
        if (null == toolbar) {
            throw new RuntimeException("Extending BaseActivity without attaching a Toolbar.");
        }

        // TODO : comment this in production
        if (null == this.drawerLayout) {
            throw new RuntimeException("Extending BaseActivity without attaching a DrawerLayout.");
        }

        // TODO : comment this in production
        if (null == this.navigationView) {
            throw new RuntimeException("Extending BaseActivity without attaching a NavigationView.");
        }

        this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, toolbar, string.drawer_open, string.drawer_close);
        this.drawerLayout.setDrawerListener(this.drawerToggle);

        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // setup drawer items clicks
        this.navigationView.setNavigationItemSelectedListener(this);

        this.drawerToggle.syncState();

        return toolbar;
    }

    /**
     * Tints the text and icon of this menu item.
     *
     * @param menuItemId the id of the menu that will be tinted
     */
    public void setDrawerSelectedItem(int menuItemId) {
        MenuItem item = this.navigationView.getMenu().findItem(menuItemId);
        if (item != null) {
            item.setChecked(true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent intent = null;
        boolean shouldFinish = true;

        if (this.shouldPerformNavigationClick(menuItem)) {
            switch (menuItem.getItemId()) {
                case id.navigation_recipes:
                    intent = new Intent(this, RecipesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
                case id.navigation_ingredients:
                    intent = new Intent(this, IngredientsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
                case id.navigation_virtual_basket:
                    intent = new Intent(this, VirtualBasketsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;

                case id.navigation_settings:
                    intent = new Intent(this, SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    break;
                /*
                case id.navigation_help:
                    intent = new Intent(this, HelpActivity.class);
                    shouldFinish = false;
                    break;
                case id.navigation_feedback:
                    intent = new Intent(this, FeedbackActivity.class);
                    shouldFinish = false;
                    break;*/
            }
        }

        this.drawerLayout.closeDrawers();

        if (intent != null) {
            this.startActivity(intent);

            if (shouldFinish) {
                this.finish();
                this.overridePendingTransition(0, 0);
            }
        }

        return false;
    }

    public abstract boolean shouldPerformNavigationClick(MenuItem menuItem);

}
