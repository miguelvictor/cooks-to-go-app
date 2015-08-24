package team.jcandfriends.cookstogo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.adapters.RecipeIngredientsAdapter;

public class RecipeIngredientsFragment extends Fragment {

    /**
     * Returns the appropriate fragment for this given position in the tabs in the Recipe Activity
     *
     * @param position What position this fragment is used for (0 - 2)
     * @return The fragment for the appropriate position
     */
    public static RecipeIngredientsFragment newInstance(int position, int recipeId) {
        Utils.log("Initializing fragment for position " + position);
        Utils.log("RecipeJson: " + recipeId);
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.RECIPE_COMPONENT_POSITION, position);
        args.putInt(Constants.EXTRA_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        JSONObject recipe;
        View view = null;

        try {
            recipe = Data.getCachedRecipe(getActivity(), args.getInt(Constants.EXTRA_RECIPE_ID));
            view = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            RecipeIngredientsAdapter ingredientsAdapter = new RecipeIngredientsAdapter(recipe.optJSONArray(Api.RECIPE_RECIPE_COMPONENTS));
            recyclerView.setAdapter(ingredientsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}