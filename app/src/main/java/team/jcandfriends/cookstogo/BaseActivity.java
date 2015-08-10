package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    protected void setUpUI () {
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

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // setup drawer items clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                        default:
                            intent = new Intent(BaseActivity.this, FeedbackActivity.class);
                            shouldFinish = false;
                    }
                }

                drawerLayout.closeDrawers();

                if (intent != null) {
                    startActivity(intent);

                    if (shouldFinish) {
                        finish();
                    }
                }

                return false;
            }
        });
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
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void setDrawerSelectedItem (int menuItemId) {
        MenuItem item = navigationView.getMenu().findItem(menuItemId);
        if (item != null) {
            item.setChecked(true);
        }
    }

    public abstract boolean shouldPerformNavigationClick(MenuItem menuItem);

}
