package team.jcandfriends.cookstogo.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;

public class RecipeSummaryFragment extends Fragment {

    public static Fragment newInstance(int position, int recipeId) {
        RecipeSummaryFragment fragment = new RecipeSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.RECIPE_COMPONENT_POSITION, position);
        args.putInt(Constants.EXTRA_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        View summaryView = null;
        JSONObject recipe;

        try {
            recipe = Data.getCachedRecipe(getActivity(), args.getInt(Constants.EXTRA_RECIPE_ID));
            summaryView = inflater.inflate(R.layout.fragment_recipe_summary, container, false);
            final View finalSummaryView = summaryView;
            ImageLoader.getInstance().loadImage(recipe.optString(Api.RECIPE_BANNER), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView banner = (ImageView) finalSummaryView.findViewById(R.id.recipe_banner);
                    banner.setImageBitmap(loadedImage);
                    Utils.decorateToolbarAndTabs(getActivity(), loadedImage);
                }
            });
            ((TextView) summaryView.findViewById(R.id.recipe_summary)).setText(recipe.optString(Api.RECIPE_DESCRIPTION));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return summaryView;
    }
}
