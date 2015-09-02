package team.jcandfriends.cookstogo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.interfaces.ToolbarGettable;

/**
 * The activity that displays the ingredient
 */
public class IngredientActivity extends AppCompatActivity implements ToolbarGettable {

    public static final String EXTRA_INGREDIENT_NAME = "extra_ingredient_name";
    public static final String EXTRA_INGREDIENT_PK = "extra_ingredient_pk";

    private Toolbar toolbar;

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

        try {
            JSONObject ingredient = Data.getCachedIngredient(this, data.getIntExtra(EXTRA_INGREDIENT_PK, -1));

            ((TextView) findViewById(R.id.ingredient_description)).setText(ingredient.optString(Api.INGREDIENT_DESCRIPTION));
            final ImageView banner = (ImageView) findViewById(R.id.ingredient_banner);
            ImageLoader.getInstance().loadImage(ingredient.optString(Api.INGREDIENT_BANNER), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    banner.setImageBitmap(loadedImage);
                    Utils.decorateToolbar(IngredientActivity.this, loadedImage);
                }
            });
        } catch (JSONException e) {
            Utils.log("JSONException : " + e.getMessage());
            e.printStackTrace();
        }
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
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
}
