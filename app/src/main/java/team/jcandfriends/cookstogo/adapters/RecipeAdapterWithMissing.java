package team.jcandfriends.cookstogo.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;

public class RecipeAdapterWithMissing extends RecyclerView.Adapter<RecipeAdapterWithMissing.RecipeAdapterWithMissingViewHolder> {

    private static final String NEARLY_THERE_RECIPE = "recipe";
    private static final String NEARLY_THERE_MISSING_COUNT = "missing_count";

    private JSONArray nearlyThereRecipes;

    public RecipeAdapterWithMissing(JSONArray nearlyThereRecipes) {
        this.nearlyThereRecipes = nearlyThereRecipes;
    }

    @Override
    public RecipeAdapterWithMissingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recipe_with_missing, viewGroup, false);
        return new RecipeAdapterWithMissingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeAdapterWithMissingViewHolder viewHolder, int i) {
        JSONObject recipe = nearlyThereRecipes.optJSONObject(i).optJSONObject(NEARLY_THERE_RECIPE);
        int missing = nearlyThereRecipes.optJSONObject(i).optInt(NEARLY_THERE_MISSING_COUNT);

        viewHolder.name.setText(recipe.optString(Api.RECIPE_NAME));
        viewHolder.description.setText(recipe.optString(Api.RECIPE_DESCRIPTION));
        viewHolder.missing.setText(missing + " ingredients missing");

        ImageLoader.getInstance().loadImage(recipe.optString(Api.RECIPE_BANNER), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                viewHolder.avatar.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearlyThereRecipes.length();
    }

    public static class RecipeAdapterWithMissingViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name;
        TextView description;
        TextView missing;

        public RecipeAdapterWithMissingViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.primary_text);
            description = (TextView) itemView.findViewById(R.id.secondary_text);
            missing = (TextView) itemView.findViewById(R.id.missing_text);
        }
    }

}
