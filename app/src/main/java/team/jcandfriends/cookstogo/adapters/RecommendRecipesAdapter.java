package team.jcandfriends.cookstogo.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.Utils.SimpleClickListener;
import team.jcandfriends.cookstogo.managers.RecipeManager;

public class RecommendRecipesAdapter extends FragmentPagerAdapter {

    private final JSONObject mResult;

    public RecommendRecipesAdapter(FragmentManager fm, JSONObject result) {
        super(fm);
        mResult = result;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            JSONArray exactRecipes = mResult.optJSONArray(Api.EXACT);
            return RecommendRecipesAdapter.RecommendRecipesFragment.newInstance(position, exactRecipes);
        } else {
            JSONArray nearlyThereRecipes = mResult.optJSONArray(Api.NEARLY_THERE);
            return RecommendRecipesAdapter.RecommendRecipesFragment.newInstance(position, nearlyThereRecipes);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return Constants.RECOMMEND_RECIPES_EXACT;
        } else {
            return Constants.RECOMMEND_RECIPES_NEARLY_THERE;
        }
    }

    public static class RecommendRecipesFragment extends Fragment {

        private static final String TAG = "RecommendRecipesFragment";

        public static Fragment newInstance(int position, JSONArray recipes) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.RECOMMEND_RECIPE_FRAGMENT_POSITION, position);
            bundle.putString(Constants.RECIPES_IN_FRAGMENT, recipes.toString());

            Fragment fragment = new RecommendRecipesAdapter.RecommendRecipesFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int fragmentPosition = arguments.getInt(Constants.RECOMMEND_RECIPE_FRAGMENT_POSITION);
            final Activity activity = getActivity();

            try {
                String recipesAsString = arguments.getString(Constants.RECIPES_IN_FRAGMENT);
                final JSONArray recipes = new JSONArray(recipesAsString);

                if (recipes.length() > 0) {
                    RecyclerView list = (RecyclerView) LayoutInflater.from(container.getContext()).inflate(layout.recommend_recipe_list, container, false);

                    if (fragmentPosition == 0) {
                        list.setAdapter(new RecipeAdapter(recipes));
                    } else {
                        list.setAdapter(new RecipeAdapterWithMissing(recipes));
                    }

                    list.setHasFixedSize(true);
                    list.setLayoutManager(new LinearLayoutManager(activity));
                    Utils.setOnItemClickListener(list, new SimpleClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            JSONObject recipe = recipes.optJSONObject(position);

                            if (fragmentPosition != 0) {
                                recipe = recipes.optJSONObject(position).optJSONObject(Api.NEARLY_THERE_RECIPE);
                            }

                            RecipeManager.get(activity).cacheRecipe(recipe);
                            Utils.startRecipeActivity(activity, recipe.optInt(Api.RECIPE_PK), recipe.optString(Api.RECIPE_NAME));
                        }
                    });
                    return list;
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            }

            return LayoutInflater.from(container.getContext()).inflate(layout.recommend_recipe_empty, container, false);
        }
    }
}
