package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import team.jcandfriends.cookstogo.interfaces.ToolbarGettable;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

/**
 * The activity that displays the ingredient
 */
public class IngredientActivity extends AppCompatActivity implements ToolbarGettable {

    public static final String EXTRA_INGREDIENT_NAME = "extra_ingredient_name";
    public static final String EXTRA_INGREDIENT_PK = "extra_ingredient_pk";

    private Toolbar toolbar;
    private JSONObject ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        Intent data = getIntent();
        String ingredientName = data.getStringExtra(EXTRA_INGREDIENT_NAME);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Utils.capitalize(ingredientName));
        }

        ingredient = IngredientManager.get(this).getCachedIngredient(data.getIntExtra(EXTRA_INGREDIENT_PK, -1));

        ((TextView) findViewById(R.id.ingredient_description)).setText(ingredient.optString(Api.INGREDIENT_DESCRIPTION));
        final ImageView banner = (ImageView) findViewById(R.id.ingredient_banner);
        ImageLoader.getInstance().loadImage(ingredient.optString(Api.INGREDIENT_BANNER), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                banner.setImageBitmap(loadedImage);
                Utils.decorateToolbar(IngredientActivity.this, loadedImage);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setStatusBarColor(this, Colors.PRIMARY_COLOR_DARK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingredient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add_to_virtual_basket:
                VirtualBasketManager manager = VirtualBasketManager.get(this);
                if (manager.isAlreadyAdded(ingredient)) {
                    Snackbar.make(findViewById(R.id.activity_parent), Utils.capitalize(ingredient.optString(Api.INGREDIENT_NAME)) + " is already added.", Snackbar.LENGTH_SHORT).show();
                } else {
                    VirtualBasketManager.get(this).add(ingredient);
                    Snackbar.make(findViewById(R.id.activity_parent), "Added " + ingredient.optString(Api.INGREDIENT_NAME), Snackbar.LENGTH_SHORT).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
}
