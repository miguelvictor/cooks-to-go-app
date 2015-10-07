package team.jcandfriends.cookstogo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Extras;
import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.adapters.RecipeStepsAdapter;
import team.jcandfriends.cookstogo.managers.RecipeManager;

/**
 * Displays all steps in a recipe
 * <p/>
 * Subordinates: fragment_recipe_methods.xml, RecipeStepsAdapter, RecipeManager
 */
public class RecipeStepsFragment extends Fragment {

    public static RecipeStepsFragment newInstance(int recipeId) {
        Bundle args = new Bundle();
        args.putInt(Extras.RECIPE_ID_EXTRA, recipeId);

        RecipeStepsFragment fragment = new RecipeStepsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        JSONObject recipe = RecipeManager.get(this.getActivity()).getCachedRecipe(args.getInt(Extras.RECIPE_ID_EXTRA));
        View view = inflater.inflate(layout.fragment_recipe_methods, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(id.recycler_view);
        RecipeStepsAdapter stepsAdapter = new RecipeStepsAdapter(recipe.optJSONArray(Api.RECIPE_STEPS));

        recyclerView.setAdapter(stepsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setHasFixedSize(true);

        return view;
    }

}
