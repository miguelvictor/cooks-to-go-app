package team.jcandfriends.cookstogo;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;

import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.RecipeManager;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpUI();
        setDrawerSelectedItem(R.id.navigation_settings);
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_settings;
    }

    public void onClearRecipesCache(View view) {
        RecipeManager.get(this).clearCachedRecipes();
        Snackbar.make(findViewById(R.id.activity_parent), "Cached recipes cleared", Snackbar.LENGTH_SHORT).show();
    }

    public void onClearIngredientsCache(View view) {
        IngredientManager.get(this).clearCachedIngredients();
        Snackbar.make(findViewById(R.id.activity_parent), "Cached ingredients cleared", Snackbar.LENGTH_SHORT).show();
    }
}
