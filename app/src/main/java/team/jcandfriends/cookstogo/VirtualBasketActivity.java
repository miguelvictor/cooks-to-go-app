package team.jcandfriends.cookstogo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.adapters.VirtualBasketItemsAdapter;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

/**
 * The activity that displays the items in the virtual basket.
 */
public class VirtualBasketActivity extends AppCompatActivity {

    private static final String TAG = "VirtualBasketActivity";

    JSONObject virtualBasket;

    private View coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_basket);

        try {
            Intent data = getIntent();
            String virtualBasketAsString = data.getStringExtra(VirtualBasketsActivity.VIRTUAL_BASKET_EXTRA);

            if (null == virtualBasketAsString) {
                finish();
                return;
            }

            virtualBasket = new JSONObject(virtualBasketAsString);

            coordinatorLayout = findViewById(id.coordinator_layout);
            View fab = findViewById(id.recommend_fab);
            View emptyView = findViewById(id.empty_view);

            Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(virtualBasket.optString(VirtualBasketManager.VIRTUAL_BASKET_NAME));

            final JSONArray virtualBasketItems = virtualBasket.optJSONArray(VirtualBasketManager.VIRTUAL_BASKET_ITEMS);

            if (virtualBasketItems.length() != 0) {
                RecyclerView recyclerView = (RecyclerView) findViewById(id.recycler_view);
                recyclerView.setAdapter(new VirtualBasketItemsAdapter(Utils.jsonArrayToList(virtualBasketItems)));
                recyclerView.setClickable(true);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setVisibility(View.VISIBLE);
                Utils.setOnItemClickListener(recyclerView, new Utils.SimpleClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        JSONObject ingredient = virtualBasketItems.optJSONObject(position);
                        Utils.startIngredientActivity(VirtualBasketActivity.this, ingredient.optInt(Api.INGREDIENT_PK), ingredient.optString(Api.INGREDIENT_NAME));
                    }
                });
                fab.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception while building a virtual basket json object from string extra", e);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_virtual_basket, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        } else if (item.getItemId() == id.action_delete_virtual_basket) {
            final VirtualBasketManager manager = VirtualBasketManager.get(this);

            new AlertDialog.Builder(this)
                    .setTitle("Delete virtual basket?")
                    .setMessage("Are you sure you want to delete " + virtualBasket.optString(VirtualBasketManager.VIRTUAL_BASKET_NAME) + "?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            manager.delete(virtualBasket);
                            finish();
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

    public void recommendRecipes(View view) {
        if (Utils.hasInternet(this)) {
            Intent intent = new Intent(this, RecommendedRecipesActivity.class);
            intent.putExtra(VirtualBasketsActivity.VIRTUAL_BASKET_EXTRA, virtualBasket.toString());
            startActivity(intent);
        } else {
            Snackbar.make(coordinatorLayout, "I'm sorry but you need internet for this.", Snackbar.LENGTH_LONG).show();
        }
    }
}
