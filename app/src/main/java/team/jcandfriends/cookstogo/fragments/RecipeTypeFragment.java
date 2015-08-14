package team.jcandfriends.cookstogo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.adapters.RecipeAdapter;

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
        Bundle args = getArguments();

        RecyclerView recipes = (RecyclerView) inflater.inflate(R.layout.fragment_recipe_type, container, false);
        try {
            recipes.setAdapter(new RecipeAdapter(getActivity(), new JSONArray(args.getString(Constants.RECIPES_IN_FRAGMENT))));
        } catch (JSONException e) {
            Utils.log("JSONException occurred while instantiating JSONArray that contains the recipes in that fragment.");
        }

        recipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipes.setItemAnimator(new DefaultItemAnimator());

        return recipes;
    }

}