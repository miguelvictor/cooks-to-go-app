package team.jcandfriends.cookstogo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.interfaces.ToolbarGettable;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

/**
 * The activity that displays the mIngredient
 */
public class IngredientActivity extends AppCompatActivity implements ToolbarGettable {

    public static final String EXTRA_INGREDIENT_NAME = "extra_ingredient_name";
    public static final String EXTRA_INGREDIENT_PK = "extra_ingredient_pk";

    private static final String TAG = "IngredientActivity";

    private boolean mIsSyncing = false;

    private JSONObject mIngredient;
    private IngredientManager mManager;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_ingredient);

        Intent data = getIntent();
        String ingredientName = data.getStringExtra(IngredientActivity.EXTRA_INGREDIENT_NAME);

        mToolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(mToolbar);

        mManager = IngredientManager.get(this);

        ActionBar actionBar = getSupportActionBar();
        assert null != actionBar;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Utils.capitalize(ingredientName));

        mIngredient = IngredientManager.get(this).getCachedIngredient(data.getIntExtra(IngredientActivity.EXTRA_INGREDIENT_PK, -1));

        synchronize(mIngredient);
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
            case id.action_add_to_virtual_basket:
                final VirtualBasketManager manager = VirtualBasketManager.get(this);
                View dialogNewVirtualBasketItem = getLayoutInflater().inflate(layout.dialog_new_virtual_basket_item, null);

                final Spinner spinner = (Spinner) dialogNewVirtualBasketItem.findViewById(R.id.spinner);
                spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, manager.getAllAsString()));

                new AlertDialog.Builder(this)
                        .setTitle("Add Ingredient to virtual basket")
                        .setView(dialogNewVirtualBasketItem)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int virtualBasketPosition = spinner.getSelectedItemPosition();
                                if (manager.isAlreadyAddedTo(virtualBasketPosition, mIngredient)) {
                                    Snackbar.make(findViewById(id.activity_parent), mIngredient.optString(Api.INGREDIENT_NAME) + " was already added to " + spinner.getSelectedItem(), Snackbar.LENGTH_LONG).show();
                                } else {
                                    manager.addTo(virtualBasketPosition, mIngredient);
                                    Snackbar.make(findViewById(id.activity_parent), "Added " + mIngredient.optString(Api.INGREDIENT_NAME) + " to " + spinner.getSelectedItem(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
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
                        URL url = new URL(Api.INGREDIENTS + mIngredient.optInt(Api.INGREDIENT_PK));
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

                    if (null != result && !result.equalsIgnoreCase(mIngredient.toString())) {
                        try {
                            JSONObject freshIngredient = new JSONObject(result);
                            mManager.cacheIngredient(freshIngredient);
                            synchronize(freshIngredient);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response as valid JSON", e);
                        }
                    }
                }
            }.execute();
        }
    }

    private void synchronize(JSONObject ingredient) {
        final TextView txtIngredientDescription = (TextView) findViewById(id.ingredient_description);
        txtIngredientDescription.setText(ingredient.optString(Api.INGREDIENT_DESCRIPTION));

        final ImageView imgIngredientBanner = (ImageView) findViewById(id.ingredient_banner);
        ImageLoader.getInstance().loadImage(ingredient.optString(Api.INGREDIENT_BANNER), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imgIngredientBanner.setImageBitmap(loadedImage);
                Utils.decorateToolbar(IngredientActivity.this, loadedImage);
            }
        });
    }
}
