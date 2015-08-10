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

import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.RecipeSearchActivity;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.adapters.IngredientTypesAdapter;
import team.jcandfriends.cookstogo.adapters.RecipeTypesAdapter;

public class IngredientsFragment extends ExtendedFragment {

    public static final String LABEL = "Ingredients";

    private boolean isList = false;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new IngredientTypesAdapter(getActivity().getSupportFragmentManager()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ingredients, menu);

        MenuItem toggleViewItem = menu.findItem(R.id.action_toggle_view);
        boolean isList = Utils.getPersistedBoolean(getActivity(), Constants.VIEW_TYPE, true);

        if (!isList) {
            this.isList = false;
            toggleViewItem.setIcon(R.mipmap.ic_view_agenda_white_24dp);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_view:
                if (isList) {
                    isList = false;
                    item.setIcon(R.mipmap.ic_view_agenda_white_24dp);
                    Utils.persistBoolean(getActivity(), Constants.VIEW_TYPE, false);
                } else {
                    isList = true;
                    item.setIcon(R.mipmap.ic_view_quilt_white_24dp);
                    Utils.persistBoolean(getActivity(), Constants.VIEW_TYPE, true);
                }
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