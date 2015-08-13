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
import team.jcandfriends.cookstogo.adapters.IngredientAdapter;

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

        RecyclerView ingredients = (RecyclerView) inflater.inflate(R.layout.fragment_ingredient_type, container, false);
        try {
            JSONArray ingredientObjects = new JSONArray(args.getString(Constants.INGREDIENTS_IN_FRAGMENT));
            ingredients.setAdapter(new IngredientAdapter(ingredientObjects));
        } catch (JSONException e) {
            Utils.log("JSONException occurred while instantiating JSONArray that contains the ingredients in that fragment.");
        }
        ingredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredients.setItemAnimator(new DefaultItemAnimator());
        return ingredients;
    }

}
