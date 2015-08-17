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
import team.jcandfriends.cookstogo.adapters.RecipeStepsAdapter;

public class RecipeStepsFragment extends Fragment {

    public static RecipeStepsFragment newInstance(int position, int recipeId) {
        Utils.log("Initializing fragment for position " + position);
        Utils.log("RecipeJson: " + recipeId);
        RecipeStepsFragment fragment = new RecipeStepsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.RECIPE_COMPONENT_POSITION, position);
        args.putInt(Constants.EXTRA_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        View view = null;
        JSONObject recipe;

        try {
            recipe = Data.getCachedRecipe(getActivity(), args.getInt(Constants.EXTRA_RECIPE_ID));
            int position = args.getInt(Constants.RECIPE_COMPONENT_POSITION);

            Utils.log("Creating fragment for position " + position);
            view = inflater.inflate(R.layout.fragment_recipe_methods, container, false);
            Utils.log("Layout inflated");
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            Utils.log("Found recyclerView");
            RecipeStepsAdapter stepsAdapter = new RecipeStepsAdapter(recipe.optJSONArray(Api.RECIPE_STEPS));
            Utils.log("Initialized stepsAdapter");
            recyclerView.setAdapter(stepsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            Utils.log("Success creating fragment for position " + position);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}
