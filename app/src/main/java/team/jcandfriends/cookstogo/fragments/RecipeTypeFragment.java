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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.adapters.RecipeAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;
import team.jcandfriends.cookstogo.tasks.FetchRecipeTask;

/**
 * RecipeTypeFragment displays all recipes of a specific recipe type.
 * <p/>
 * Subordinates: fragment_recipe_type.xml, RecipeAdapter, Data, Utils, FetchRecipeTask
 */
public final class RecipeTypeFragment extends Fragment {

    public static RecipeTypeFragment newInstance(JSONArray recipes) {
        RecipeTypeFragment fragment = new RecipeTypeFragment();
        Bundle args = new Bundle();
        args.putString(Constants.RECIPES_IN_FRAGMENT, recipes.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final RecipeManager recipeManager = RecipeManager.get(activity);
        Bundle args = getArguments();
        RecyclerView recipes = (RecyclerView) inflater.inflate(R.layout.fragment_recipe_type, container, false);

        try {
            final JSONArray recipesArray = new JSONArray(args.getString(Constants.RECIPES_IN_FRAGMENT));
            recipes.setAdapter(new RecipeAdapter(recipesArray));
            recipes.setLayoutManager(new LinearLayoutManager(activity));
            recipes.setItemAnimator(new DefaultItemAnimator());
            recipes.setHasFixedSize(true);
            Utils.setOnItemClickListener(recipes, new Utils.SimpleClickListener() {
                @Override
                public void onClick(View view, int position) {
                    JSONObject recipe = recipesArray.optJSONObject(position);
                    final int recipeId = recipe.optInt(Api.RECIPE_PK);
                    final String recipeName = recipe.optString(Api.RECIPE_NAME);

                    if (Utils.hasInternet(activity)) {
                        final AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle(R.string.dialog_recipe_loading_header)
                                .setMessage(R.string.dialog_recipe_loading_subheader)
                                .setCancelable(false)
                                .create();

                        dialog.show();
                        recipeManager.fetch(recipeId, new RecipeManager.Callbacks() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                dialog.dismiss();
                                recipeManager.cacheRecipe(result);
                                Utils.startRecipeActivity(activity, recipeId, recipeName);
                            }

                            @Override
                            public void onFailure() {
                                dialog.dismiss();
                                Toast.makeText(activity, "Some unexpected error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        FetchRecipeTask.start(recipeId, new FetchRecipeTask.Callbacks() {
                            @Override
                            public void onPreExecute() {
                                dialog.show();
                            }

                            @Override
                            public void onPostExecute(JSONObject recipe) {
                                dialog.dismiss();
                                Data.cacheRecipe(activity, recipe);
                                Utils.startRecipeActivity(activity, recipeId, recipeName);
                            }
                        });
                    } else if (Data.hasCachedRecipe(activity, recipeId)) {
                        Utils.startRecipeActivity(activity, recipeId, recipeName);
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
            });
        } catch (JSONException e) {
            Utils.log("JSONException occurred while instantiating JSONArray that contains the recipes in that fragment.");
        }

        return recipes;
    }

}