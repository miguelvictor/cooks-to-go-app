package team.jcandfriends.cookstogo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.adapters.IngredientAdapter;

public class IngredientTypeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView ingredients = (RecyclerView) inflater.inflate(R.layout.fragment_ingredient_type, container, false);
        ingredients.setAdapter(new IngredientAdapter());
        ingredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        return ingredients;
    }

}
