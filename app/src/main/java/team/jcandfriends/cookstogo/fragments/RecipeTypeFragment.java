package team.jcandfriends.cookstogo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.adapters.RecipeAdapter;

public final class RecipeTypeFragment extends Fragment {

    public static RecipeTypeFragment newInstance(int position) {
        RecipeTypeFragment fragment = new RecipeTypeFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.FRAGMENT_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        RecyclerView recipes = (RecyclerView) inflater.inflate(R.layout.fragment_recipe_type, container, false);

        // recipes.setAdapter(new RecipeAdapter(Data.recipes.all()));
        recipes.setAdapter(new RecipeAdapter(null));
        recipes.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recipes;
    }

}
