package team.jcandfriends.cookstogo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.karim.MaterialTabs;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.RecipeSearchActivity;
import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;

public class RecipesFragment extends ExtendedFragment {

    public static final String LABEL = "Recipes";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                Intent intent = new Intent(getActivity(), RecipeSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getFragmentTitle() {
        return LABEL;
    }

}