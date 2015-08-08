package team.jcandfriends.cookstogo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class Fragments {

    public static abstract class ExtendedFragment extends Fragment {

        public abstract String getFragmentTitle();

    }

    public static class HomeFragment extends ExtendedFragment {

        public static final String LABEL = "Home";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_home, container, false);
        }

        @Override
        public String getFragmentTitle() {
            return LABEL;
        }
    }

    public static class RecipesFragment extends ExtendedFragment {

        public static final String LABEL = "Recipes";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_recipes, container, false);
        }

        @Override
        public String getFragmentTitle() {
            return LABEL;
        }

    }

    public static class IngredientsFragment extends ExtendedFragment {

        public static final String LABEL = "Ingredients";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ingredients, container, false);
        }

        @Override
        public String getFragmentTitle() {
            return LABEL;
        }

    }

    public static class VirtualBasketFragment extends ExtendedFragment {

        public static final String LABEL = "Virtual Basket";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_virtual_basket, container, false);
        }

        @Override
        public String getFragmentTitle() {
            return LABEL;
        }

    }

    public static class SettingsFragment extends ExtendedFragment {

        public static final String LABEL = "Settings";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_settings, container, false);
        }

        @Override
        public String getFragmentTitle() {
            return LABEL;
        }

    }


}
