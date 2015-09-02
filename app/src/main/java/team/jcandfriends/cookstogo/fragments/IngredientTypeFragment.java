package team.jcandfriends.cookstogo.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.BaseActivity;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.VirtualBasketContainer;
import team.jcandfriends.cookstogo.adapters.IngredientAdapter;
import team.jcandfriends.cookstogo.tasks.FetchIngredientTask;

/**
 * IngredientTypeFragment displays all ingredients of a specific ingredient type.
 * <p/>
 * Subordinates: fragment_ingredient_type.xml, IngredientAdapter, Data, Utils
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
        Bundle args = getArguments();
        final RecyclerView ingredients;

        ingredients = (RecyclerView) inflater.inflate(R.layout.fragment_ingredient_type, container, false);
        try {
            final JSONArray ingredientsArray = new JSONArray(args.getString(Constants.INGREDIENTS_IN_FRAGMENT));
            IngredientAdapter ingredientAdapter = new IngredientAdapter(ingredientsArray);
            ingredients.setAdapter(ingredientAdapter);
            ingredients.setLayoutManager(new LinearLayoutManager(getActivity()));
            ingredients.setItemAnimator(new DefaultItemAnimator());
            Utils.setOnItemClickListener(ingredients, new Utils.SimpleClickListener() {
                @Override
                public void onClick(View view, int position) {
                    final Activity activity = getActivity();

                    JSONObject ingredient = ingredientsArray.optJSONObject(position);
                    final int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);
                    final String ingredientName = ingredient.optString(Api.INGREDIENT_NAME);

                    if (Utils.hasInternet(activity)) {
                        final AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle(R.string.dialog_ingredient_loading_header)
                                .setMessage(R.string.dialog_ingredient_loading_subheader)
                                .setCancelable(false)
                                .create();
                        FetchIngredientTask.start(ingredientId, new FetchIngredientTask.Callbacks() {
                            @Override
                            public void onPreExecute() {
                                dialog.show();
                            }

                            @Override
                            public void onPostExecute(JSONObject ingredient) {
                                dialog.dismiss();
                                Data.cacheIngredient(activity, ingredient);
                                Utils.startIngredientActivity(activity, ingredientId, ingredientName);
                            }
                        });
                    } else if (Data.hasCachedRecipe(activity, ingredientId)) {
                        Utils.startIngredientActivity(activity, ingredientId, ingredientName);
                    } else {
                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.dialog_no_internet_header)
                                .setMessage(R.string.dialog_no_internet_subheader)
                                .setNeutralButton(R.string.dialog_neutral_button_label, new DialogInterface.OnClickListener() {
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
                public void onLongClick(View view, final int position) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Add Ingredient")
                            .setMessage("Add this ingredient to virtual basket?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = getActivity();
                                    JSONObject ingredient = ingredientsArray.optJSONObject(position);

                                    if (VirtualBasketContainer.isAlreadyAdded(activity, ingredient)) {
                                        Utils.showSnackbar((BaseActivity) activity, Utils.capitalize(ingredient.optString(Api.INGREDIENT_NAME)) + " is already added");
                                    } else {
                                        VirtualBasketContainer.addItem(ingredient);
                                        Utils.showSnackbar((BaseActivity) activity, "Added " + ingredient.optString(Api.INGREDIENT_NAME));
                                        VirtualBasketContainer.persist(activity);
                                    }
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
        } catch (JSONException e) {
            Utils.log("JSONException occurred while instantiating JSONArray that contains the ingredients in that fragment.");
        }

        return ingredients;
    }

}
