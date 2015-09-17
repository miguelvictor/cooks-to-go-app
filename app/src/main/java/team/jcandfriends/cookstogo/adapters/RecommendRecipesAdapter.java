package team.jcandfriends.cookstogo.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.managers.RecipeManager;

public class RecommendRecipesAdapter extends FragmentPagerAdapter {

    private JSONObject result;

    public RecommendRecipesAdapter(FragmentManager fm, JSONObject result) {
        super(fm);
        this.result = result;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            JSONArray exactRecipes = result.optJSONArray(Api.EXACT);
            return RecommendRecipesFragment.newInstance(position, exactRecipes);
        } else {
            final JSONArray nearlyThereRecipes = result.optJSONArray(Api.NEARLY_THERE);
            return RecommendRecipesFragment.newInstance(position, nearlyThereRecipes);
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

        public static Fragment newInstance(int position, JSONArray recipes) {
            Fragment fragment = new RecommendRecipesFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.RECOMMEND_RECIPE_FRAGMENT_POSITION, position);
            bundle.putString(Constants.RECIPES_IN_FRAGMENT, recipes.toString());
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final Bundle arguments = getArguments();
            final int fragmentPosition = arguments.getInt(Constants.RECOMMEND_RECIPE_FRAGMENT_POSITION);
            final Activity activity = getActivity();

            try {
                Utils.log(getArguments().getString(Constants.RECIPES_IN_FRAGMENT));
                String recipesAsString = arguments.getString(Constants.RECIPES_IN_FRAGMENT);
                final JSONArray recipes = new JSONArray(recipesAsString);

                if (recipes.length() > 0) {
                    RecyclerView list = (RecyclerView) LayoutInflater.from(container.getContext()).inflate(R.layout.recommend_recipe_list, container, false);

                    if (fragmentPosition == 0) {
                        list.setAdapter(new RecipeAdapter(recipes));
                    } else {
                        list.setAdapter(new RecipeAdapterWithMissing(recipes));
                    }

                    list.setClickable(true);
                    list.setHasFixedSize(true);
                    list.setLayoutManager(new LinearLayoutManager(activity));
                    Utils.setOnItemClickListener(list, new Utils.SimpleClickListener() {
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
                e.printStackTrace();
            }

            return LayoutInflater.from(container.getContext()).inflate(R.layout.recommend_recipe_empty, container, false);
        }
    }
}
