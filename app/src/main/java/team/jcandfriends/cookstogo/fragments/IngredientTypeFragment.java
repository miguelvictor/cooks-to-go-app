package team.jcandfriends.cookstogo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.BaseActivity;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.Extras;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.Utils.SimpleClickListener;
import team.jcandfriends.cookstogo.VirtualBasketsActivity;
import team.jcandfriends.cookstogo.adapters.IngredientAdapter;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.IngredientManager.Callbacks;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

/**
 * IngredientTypeFragment displays all ingredients of a specific ingredient type.
 * <p/>
 * Subordinates: fragment_ingredient_type.xml, IngredientAdapter, IngredientManager, Utils
 */
public class IngredientTypeFragment extends Fragment {

    public static final int ADD_NEW_VIRTUAL_BASKET_REQUEST_CODE = 1;

    public static IngredientTypeFragment newInstance(JSONArray ingredients) {
        Bundle args = new Bundle();
        args.putString(Constants.INGREDIENTS_IN_FRAGMENT, ingredients.toString());

        IngredientTypeFragment fragment = new IngredientTypeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        RecyclerView ingredients;

        final FragmentActivity activity = this.getActivity();
        final IngredientManager ingredientManager = IngredientManager.get(activity);
        final VirtualBasketManager virtualBasketManager = VirtualBasketManager.get(activity);

        ingredients = (RecyclerView) inflater.inflate(R.layout.fragment_ingredient_type, container, false);

        try {
            final JSONArray ingredientsArray = new JSONArray(args.getString(Constants.INGREDIENTS_IN_FRAGMENT));
            IngredientAdapter ingredientAdapter = new IngredientAdapter(ingredientsArray);
            ingredients.setAdapter(ingredientAdapter);
            ingredients.setLayoutManager(new LinearLayoutManager(activity));
            ingredients.setItemAnimator(new DefaultItemAnimator());
            Utils.setOnItemClickListener(ingredients, new SimpleClickListener() {
                @Override
                public void onClick(View view, int position) {
                    JSONObject ingredient = ingredientsArray.optJSONObject(position);
                    Intent data = activity.getIntent();

                    if (null != data && VirtualBasketManager.ADD_INGREDIENT_TO_VIRTUAL_BASKET.equals(data.getAction())) {
                        addIngredientToBasket(activity, data.getIntExtra(Extras.VIRTUAL_BASKET_POSITION_EXTRA, -1), ingredient);
                    } else {
                        displayIngredient(activity, ingredient);
                    }
                }

                @Override
                public void onLongClick(final View view, final int ingredientPosition) {
                    final JSONObject ingredient = ingredientsArray.optJSONObject(ingredientPosition);
                    final List<String> virtualBaskets = virtualBasketManager.getAllAsString();

                    /**
                     * exit prematurely if the user has no virtual baskets
                     */
                    if (virtualBaskets.size() == 0) {
                        ingredientManager.saveIngredient(ingredient);
                        suggestToAddNewVirtualBasket(activity);
                        return;
                    }

                    onAddToVirtualBasket(activity, ingredient);
                }
            });
        } catch (JSONException e) {
            Utils.log("JSONException occurred while instantiating JSONArray that contains the ingredients in that fragment.");
        }

        return ingredients;
    }

    private void addIngredientToBasket(Activity activity, int basketPosition, JSONObject ingredient) {
        VirtualBasketManager virtualBasketManager = VirtualBasketManager.get(activity);

        if (virtualBasketManager.isAlreadyAddedTo(basketPosition, ingredient)) {
            Utils.showSnackbar((BaseActivity) getActivity(), ingredient.optString(Api.INGREDIENT_NAME) + " is already added.");
        } else {
            virtualBasketManager.addTo(basketPosition, ingredient);
            Intent data = new Intent();
            data.putExtra(Extras.INGREDIENT_EXTRA, ingredient.toString());
            activity.setResult(Activity.RESULT_OK, data);
            activity.finish();
        }
    }

    private void displayIngredient(final FragmentActivity activity, final JSONObject ingredient) {
        final int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);
        final String ingredientName = ingredient.optString(Api.INGREDIENT_NAME);
        final IngredientManager ingredientManager = IngredientManager.get(activity);

        if (ingredientManager.hasCachedIngredient(ingredientId)) {
            Utils.startIngredientActivity(activity, ingredientId, ingredientName);
        } else if (Utils.hasInternet(activity)) {
            final AlertDialog dialog = new Builder(activity)
                    .setTitle(R.string.dialog_ingredient_loading_header)
                    .setMessage(R.string.dialog_ingredient_loading_subheader)
                    .setCancelable(false)
                    .create();

            dialog.show();
            ingredientManager.fetch(ingredientId, new Callbacks() {
                @Override
                public void onSuccess(JSONObject result) {
                    dialog.dismiss();
                    ingredientManager.cacheIngredient(result);
                    Utils.startIngredientActivity(activity, ingredientId, ingredientName);
                }

                @Override
                public void onFailure() {
                    dialog.dismiss();
                    Toast.makeText(activity, "Some unexpected error occurred.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            new Builder(activity)
                    .setTitle(R.string.dialog_no_internet_header)
                    .setMessage(R.string.dialog_no_internet_subheader)
                    .setNeutralButton(R.string.dialog_neutral_button_label, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void suggestToAddNewVirtualBasket(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("No Virtual Baskets")
                .setMessage("It seems like you haven't created any virtual baskets yet. Would you like to create one now?")
                .setPositiveButton("Yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent = new Intent(context, VirtualBasketsActivity.class);
                        intent.setAction(VirtualBasketManager.ADD_NEW_VIRTUAL_BASKET);
                        startActivityForResult(intent, ADD_NEW_VIRTUAL_BASKET_REQUEST_CODE);
                    }
                })
                .setNegativeButton("No", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NEW_VIRTUAL_BASKET_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Activity activity = getActivity();
            IngredientManager manager = IngredientManager.get(activity);

            onAddToVirtualBasket(activity, manager.getSavedIngredient());
            manager.forgetSavedIngredient();
        }
    }

    private void onAddToVirtualBasket(final Activity activity, final JSONObject ingredient) {
        final View dialogNewVirtualBasketItem = activity.getLayoutInflater().inflate(R.layout.dialog_new_virtual_basket_item, null);
        final Spinner spinner = (Spinner) dialogNewVirtualBasketItem.findViewById(R.id.spinner);
        final VirtualBasketManager virtualBasketManager = VirtualBasketManager.get(activity);

        spinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, virtualBasketManager.getAllAsString()));

        new Builder(activity)
                .setTitle("Add Ingredient to virtual basket")
                .setView(dialogNewVirtualBasketItem)
                .setPositiveButton("Add", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int virtualBasketPosition = spinner.getSelectedItemPosition();
                        if (virtualBasketManager.isAlreadyAddedTo(virtualBasketPosition, ingredient)) {
                            Utils.showSnackbar((BaseActivity) activity, ingredient.optString(Api.INGREDIENT_NAME) + " was already added to " + spinner.getSelectedItem());
                        } else {
                            virtualBasketManager.addTo(virtualBasketPosition, ingredient);
                            Utils.showSnackbar((BaseActivity) activity, "Added " + ingredient.optString(Api.INGREDIENT_NAME) + " to " + spinner.getSelectedItem());
                        }
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
