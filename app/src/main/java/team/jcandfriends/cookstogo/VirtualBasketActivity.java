package team.jcandfriends.cookstogo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.adapters.VirtualBasketItemsAdapter;
import team.jcandfriends.cookstogo.tasks.RecommendRecipesTask;

/**
 * The activity that displays the items in the virtual basket.
 */
public class VirtualBasketActivity extends BaseActivity {

    private Toolbar toolbar;
    private VirtualBasketItemsAdapter adapter;
    private FloatingActionButton recommendFab;
    private ArrayList<JSONObject> ingredients;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_basket);
        toolbar = setUpUI();
        setDrawerSelectedItem(R.id.navigation_virtual_basket);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);

        recommendFab = (FloatingActionButton) findViewById(R.id.recommend_fab);

        VirtualBasketContainer.initialize(this);
        ingredients = VirtualBasketContainer.getData();

        setupRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_virtual_basket, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, RecipeSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            case R.id.action_delete_all:
                new AlertDialog.Builder(this)
                        .setTitle("Delete all?")
                        .setMessage("This will delete all the items in your virtual basket. Proceed with caution.")
                        .setPositiveButton("Delete 'em", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VirtualBasketContainer.deleteAll(VirtualBasketActivity.this);
                                adapter.deleteAll();
                                Utils.showSnackbar(VirtualBasketActivity.this, "All items deleted");
                                checkAdapterIsEmpty(adapter);
                                Utils.log("DELETE ALL : current items count -> " + adapter.getItemCount());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_virtual_basket;
    }

    private void checkAdapterIsEmpty(VirtualBasketItemsAdapter adapter) {
        if (adapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recommendFab.setVisibility(View.GONE);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
        } else {
            emptyView.setVisibility(View.GONE);
            recommendFab.setVisibility(View.VISIBLE);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        }
    }

    protected void setupRecyclerView(RecyclerView recyclerView) {
        adapter = new VirtualBasketItemsAdapter(ingredients);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkAdapterIsEmpty(adapter);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Utils.setOnItemClickListener(recyclerView, new Utils.SimpleClickListener() {
            @Override
            public void onClick(View view, int position) {
                JSONObject ingredient = ingredients.get(position);
                Utils.startIngredientActivity(VirtualBasketActivity.this, ingredient.optInt(Api.INGREDIENT_PK), ingredient.optString(Api.INGREDIENT_NAME));
            }

            @Override
            public void onLongClick(View view, final int position) {
                final JSONObject ingredient = ingredients.get(position);
                new AlertDialog.Builder(VirtualBasketActivity.this)
                        .setTitle("Remove " + ingredient.optString(Api.INGREDIENT_NAME) + "?")
                        .setMessage("This will remove the item in your virtual basket")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VirtualBasketContainer.removeItem(ingredient);
                                VirtualBasketContainer.persist(VirtualBasketActivity.this);
                                adapter.removeItem(ingredient);
                                adapter.notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

        checkAdapterIsEmpty(adapter);
    }

    public void recommendRecipes(View view) {
        RecommendRecipesTask.start(0, new RecommendRecipesTask.Callbacks() {
            @Override
            public void onPreExecute() {
                Utils.showSnackbar(VirtualBasketActivity.this, "Fetching recipes...");
            }

            @Override
            public void onPostExecute(JSONObject recipes) {

            }
        });
    }
}
