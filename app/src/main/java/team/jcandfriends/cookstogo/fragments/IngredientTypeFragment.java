package team.jcandfriends.cookstogo.fragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.BaseActivity;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.R.string;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.Utils.SimpleClickListener;
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

    public static IngredientTypeFragment newInstance(JSONArray ingredients) {
        IngredientTypeFragment fragment = new IngredientTypeFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INGREDIENTS_IN_FRAGMENT, ingredients.toString());
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

        ingredients = (RecyclerView) inflater.inflate(layout.fragment_ingredient_type, container, false);

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
                    final int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);
                    final String ingredientName = ingredient.optString(Api.INGREDIENT_NAME);

                    /*if (Utils.hasInternet(activity)) {
                        final AlertDialog dialog = new Builder(activity)
                                .setTitle(string.dialog_ingredient_loading_header)
                                .setMessage(string.dialog_ingredient_loading_subheader)
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
                    } else if (ingredientManager.hasCachedIngredient(ingredientId)) {
                        Utils.startIngredientActivity(activity, ingredientId, ingredientName);
                    } else {
                        new Builder(activity)
                                .setTitle(string.dialog_no_internet_header)
                                .setMessage(string.dialog_no_internet_subheader)
                                .setNeutralButton(string.dialog_neutral_button_label, new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    }*/
                    if (ingredientManager.hasCachedIngredient(ingredientId)) {
                        Utils.startIngredientActivity(activity, ingredientId, ingredientName);
                    } else if (Utils.hasInternet(activity)) {
                        final AlertDialog dialog = new Builder(activity)
                                .setTitle(string.dialog_ingredient_loading_header)
                                .setMessage(string.dialog_ingredient_loading_subheader)
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
                                .setTitle(string.dialog_no_internet_header)
                                .setMessage(string.dialog_no_internet_subheader)
                                .setNeutralButton(string.dialog_neutral_button_label, new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    }
                }

                @Override
                public void onLongClick(final View view, final int ingredientPosition) {
                    final JSONObject ingredient = ingredientsArray.optJSONObject(ingredientPosition);

                    View dialogNewVirtualBasketItem = activity.getLayoutInflater().inflate(layout.dialog_new_virtual_basket_item, null);

                    final Spinner spinner = (Spinner) dialogNewVirtualBasketItem.findViewById(R.id.spinner);
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
            });
        } catch (JSONException e) {
            Utils.log("JSONException occurred while instantiating JSONArray that contains the ingredients in that fragment.");
        }

        return ingredients;
    }

}
