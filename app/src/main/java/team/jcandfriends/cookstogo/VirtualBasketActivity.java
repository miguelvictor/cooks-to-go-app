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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.adapters.VirtualBasketItemsAdapter;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

/**
 * The activity that displays the items in the virtual basket.
 */
public class VirtualBasketActivity extends AppCompatActivity {

    public static final int ADD_INGREDIENT_REQUEST_CODE = 1;
    private static final String TAG = "VirtualBasketActivity";
    private int mVirtualBasketPosition;
    private VirtualBasketItemsAdapter mAdapter;
    private JSONObject mVirtualBasket;

    private View mCoordinatorLayout;
    private View mFab;
    private View mEmptyView;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_basket);

        try {
            Intent data = getIntent();
            String virtualBasketAsString = data.getStringExtra(Extras.VIRTUAL_BASKET_EXTRA);

            if (null == virtualBasketAsString) {
                finish();
                return;
            }

            mVirtualBasketPosition = data.getIntExtra(Extras.VIRTUAL_BASKET_POSITION_EXTRA, -1);
            mVirtualBasket = new JSONObject(virtualBasketAsString);

            mCoordinatorLayout = findViewById(R.id.coordinator_layout);
            mFab = findViewById(R.id.recommend_fab);
            mEmptyView = findViewById(R.id.empty_view);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mVirtualBasket.optString(VirtualBasketManager.VIRTUAL_BASKET_NAME));

            final IngredientManager manager = IngredientManager.get(this);

            JSONArray virtualBasketItems = mVirtualBasket.optJSONArray(VirtualBasketManager.VIRTUAL_BASKET_ITEMS);
            mAdapter = new VirtualBasketItemsAdapter(Utils.jsonArrayToList(virtualBasketItems));
            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            Utils.setOnItemClickListener(mRecyclerView, new Utils.CustomClickListener() {
                @Override
                public void onClick(View view, int position) {
                    final JSONObject ingredient = mAdapter.getItem(position);
                    final int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);

                    if (manager.hasCachedIngredient(ingredientId)) {
                        Utils.startIngredientActivity(VirtualBasketActivity.this, ingredientId, ingredient.optString(Api.INGREDIENT_NAME));
                    } else {
                        final AlertDialog dialog = new AlertDialog.Builder(VirtualBasketActivity.this)
                                .setTitle(R.string.dialog_ingredient_loading_header)
                                .setMessage(R.string.dialog_ingredient_loading_subheader)
                                .setCancelable(false)
                                .create();

                        dialog.show();
                        manager.fetch(ingredientId, new IngredientManager.Callbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                dialog.dismiss();
                                manager.cacheIngredient(result);
                                Utils.startIngredientActivity(VirtualBasketActivity.this, ingredientId, ingredient.optString(Api.INGREDIENT_NAME));
                            }

                            @Override
                            public void onFailure() {
                                dialog.dismiss();
                                Toast.makeText(VirtualBasketActivity.this, "Some unexpected error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onLongClick(View view, final int position) {
                    final JSONObject ingredient = mAdapter.getItem(position);
                    final String ingredientName = ingredient.optString(Api.INGREDIENT_NAME);

                    new AlertDialog.Builder(VirtualBasketActivity.this)
                            .setTitle("Delete " + ingredientName + "?")
                            .setMessage("Are you sure you want to remove this ingredient in this virtual basket?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    VirtualBasketManager.get(VirtualBasketActivity.this).deleteFrom(mVirtualBasketPosition, position);
                                    mAdapter.removeItem(position);
                                    synchronizeView();
                                    Snackbar.make(mCoordinatorLayout, "Deleted " + ingredientName, Snackbar.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                }
            });

            synchronizeView();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete_virtual_basket:
                new AlertDialog.Builder(this)
                        .setTitle("Delete virtual basket?")
                        .setMessage("Are you sure you want to delete " + mVirtualBasket.optString(VirtualBasketManager.VIRTUAL_BASKET_NAME) + "?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VirtualBasketManager.get(VirtualBasketActivity.this).delete(mVirtualBasket);
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
            case R.id.action_add_ingredient_to_virtual_basket:
                Intent intent = new Intent(this, IngredientsActivity.class);
                intent.setAction(VirtualBasketManager.ADD_INGREDIENT_TO_VIRTUAL_BASKET);
                intent.putExtra(Extras.VIRTUAL_BASKET_POSITION_EXTRA, mVirtualBasketPosition);
                startActivityForResult(intent, ADD_INGREDIENT_REQUEST_CODE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void recommendRecipes(View view) {
        if (Utils.hasInternet(this)) {
            Intent intent = new Intent(this, RecommendedRecipesActivity.class);
            intent.putExtra(Extras.VIRTUAL_BASKET_EXTRA, mVirtualBasket.toString());
            startActivity(intent);
        } else {
            Snackbar.make(mCoordinatorLayout, "I'm sorry but you need internet for this.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_INGREDIENT_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                JSONObject ingredient = new JSONObject(data.getStringExtra(Extras.INGREDIENT_EXTRA));
                String ingredientName = ingredient.optString(Api.INGREDIENT_NAME);
                mAdapter.addItem(ingredient);
                synchronizeView();
                Snackbar.make(mCoordinatorLayout, "Added " + ingredientName, Snackbar.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing ingredientJsonAsString extra", e);
            }
        }
    }

    private void synchronizeView() {
        if (mAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mFab.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }
}
