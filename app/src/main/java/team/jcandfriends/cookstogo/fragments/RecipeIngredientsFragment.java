package team.jcandfriends.cookstogo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Extras;
import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.Utils.SimpleClickListener;
import team.jcandfriends.cookstogo.adapters.RecipeIngredientsAdapter;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.RecipeManager;

/**
 * RecipeIngredientsFragment displays all ingredients used in a recipe
 * <p/>
 * Subordinates: fragment_recipe_ingredients.xml, RecipeIngredientsAdapter
 */
public class RecipeIngredientsFragment extends Fragment {

    /**
     * Returns the appropriate fragment for this given position in the tabs in the Recipe Activity
     *
     * @return The fragment for the appropriate position
     */
    public static RecipeIngredientsFragment newInstance(int recipeId) {
        Bundle args = new Bundle();
        args.putInt(Extras.RECIPE_ID_EXTRA, recipeId);

        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Activity activity = this.getActivity();
        Bundle args = this.getArguments();
        JSONObject recipe;
        View view;

        recipe = RecipeManager.get(activity).getCachedRecipe(args.getInt(Extras.RECIPE_ID_EXTRA));
        final JSONArray recipeComponents = recipe.optJSONArray(Api.RECIPE_RECIPE_COMPONENTS);

        view = inflater.inflate(layout.fragment_recipe_ingredients, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(id.recycler_view);
        RecipeIngredientsAdapter ingredientsAdapter = new RecipeIngredientsAdapter(recipeComponents);
        recyclerView.setAdapter(ingredientsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setHasFixedSize(true);
        Utils.setOnItemClickListener(recyclerView, new SimpleClickListener() {
            @Override
            public void onClick(View view, int position) {
                JSONObject ingredient = recipeComponents.optJSONObject(position).optJSONObject(Api.RECIPE_COMPONENT_INGREDIENT);
                IngredientManager.get(activity).cacheIngredient(ingredient);
                Utils.startIngredientActivity(RecipeIngredientsFragment.this.getActivity(), ingredient.optInt(Api.INGREDIENT_PK), ingredient.optString(Api.INGREDIENT_NAME));
            }
        });

        return view;
    }

}
