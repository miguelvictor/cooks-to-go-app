package team.jcandfriends.cookstogo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.RecipeActivity;
import team.jcandfriends.cookstogo.adapters.RecipeAdapter;
import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;

public final class RecipeTypeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_type, container, false);

        ListView recipes = (ListView) view.findViewById(R.id.list_of_recipes);
        recipes.setAdapter(new RecipeAdapter());
        recipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

}
