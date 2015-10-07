package team.jcandfriends.cookstogo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.adapters.VirtualBasketAdapter;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

public class VirtualBasketsActivity extends BaseActivity {

    private VirtualBasketManager mManager;
    private VirtualBasketAdapter mAdapter;

    private View mAddVirtualBasketView;
    private EditText mVirtualBasketNameEt;
    private AlertDialog mAddVirtualBasketDialog;

    private boolean mFromIntent = false;

    /**
     * View synchronization
     */
    private View emptyView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.log("VirtualBasketsActivity : onCreate");
        setContentView(R.layout.activity_virtual_baskets);
        setUpUI();
        setDrawerSelectedItem(R.id.navigation_virtual_basket);

        emptyView = findViewById(R.id.empty_view);

        mManager = VirtualBasketManager.get(this);

        ArrayList<JSONObject> virtualBaskets = mManager.getAll();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new VirtualBasketAdapter(virtualBaskets);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Utils.setOnItemClickListener(recyclerView, new Utils.CustomClickListener() {
            @Override
            public void onClick(View view, int position) {
                JSONObject virtualBasket = mManager.get(position);
                Intent intent = new Intent(VirtualBasketsActivity.this, VirtualBasketActivity.class);
                intent.putExtra(Extras.VIRTUAL_BASKET_EXTRA, virtualBasket.toString());
                intent.putExtra(Extras.VIRTUAL_BASKET_POSITION_EXTRA, position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                final JSONObject virtualBasket = mManager.get(position);

                new AlertDialog.Builder(VirtualBasketsActivity.this)
                        .setTitle("Delete virtual basket?")
                        .setMessage("Are you sure you want to delete " + virtualBasket.optString(VirtualBasketManager.VIRTUAL_BASKET_NAME) + "? This action cannot be undone.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mManager.remove(virtualBasket);
                                mAdapter.notifyItemRemoved(position);
                                synchronizeView();
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
            }
        });

        Intent intent = getIntent();
        if (null != intent && VirtualBasketManager.ADD_NEW_VIRTUAL_BASKET.equals(intent.getAction())) {
            mFromIntent = true;
            onAddNewVirtualBasket(null);
        }
    }

    @Override
    public boolean shouldPerformNavigationClick(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.navigation_virtual_basket;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_virtual_baskets) {
            if (mManager.getCount() == 0) {
                Snackbar.make(findViewById(R.id.coordinator_layout), "There's nothing to delete", Snackbar.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Delete all?")
                        .setMessage("Are you sure you want to delete all your virtual baskets?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mManager.deleteAll();
                                mAdapter.notifyDataSetChanged();
                                synchronizeView();
                                Snackbar.make(findViewById(R.id.coordinator_layout), "Deleted all virtual baskets", Snackbar.LENGTH_LONG).show();
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
            }
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, IngredientSearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_virtual_baskets, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
        synchronizeView();
    }

    public void onAddNewVirtualBasket(View view) {
        if (mAddVirtualBasketView == null) {
            mAddVirtualBasketView = getLayoutInflater().inflate(R.layout.dialog_new_virtual_basket, null);
            mVirtualBasketNameEt = (EditText) mAddVirtualBasketView.findViewById(R.id.virtual_basket_name);
            mAddVirtualBasketDialog = new AlertDialog.Builder(this)
                    .setTitle("Add New Virtual Basket")
                    .setView(mAddVirtualBasketView)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = mVirtualBasketNameEt.getText().toString().trim();
                            if (name.isEmpty()) {
                                dialog.dismiss();
                                Snackbar.make(findViewById(R.id.coordinator_layout), "Name should not be empty.", Snackbar.LENGTH_LONG).show();
                            } else if (mManager.isAlreadyAdded(name)) {
                                Snackbar.make(findViewById(R.id.coordinator_layout), name + " was already added.", Snackbar.LENGTH_LONG).show();
                            } else {
                                mManager.add(mVirtualBasketNameEt.getText().toString());
                                mAdapter.notifyItemInserted(mAdapter.getItemCount());
                                synchronizeView();
                                dialog.dismiss();
                                Snackbar.make(findViewById(R.id.coordinator_layout), "Added " + name, Snackbar.LENGTH_LONG).show();

                                if (mFromIntent) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }

                            mVirtualBasketNameEt.setText("");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mFromIntent) {
                                setResult(RESULT_CANCELED);
                                finish();
                            }

                            dialog.dismiss();
                        }
                    })
                    .create();
        }

        mAddVirtualBasketDialog.show();
    }

    public void synchronizeView() {
        if (mAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
