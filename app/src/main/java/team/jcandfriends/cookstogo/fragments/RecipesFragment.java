package team.jcandfriends.cookstogo.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.karim.MaterialTabs;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;

public class RecipesFragment extends ExtendedFragment {

    public static final String LABEL = "Recipes";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new RecipeTypesAdapter(getActivity().getSupportFragmentManager()));

        MaterialTabs materialTabs = (MaterialTabs) view.findViewById(R.id.materialTabs);
        materialTabs.setViewPager(viewPager);

        return view;
    }

    @Override
    public String getFragmentTitle() {
        return LABEL;
    }

}