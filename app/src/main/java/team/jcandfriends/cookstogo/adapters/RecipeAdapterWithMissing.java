package team.jcandfriends.cookstogo.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.inflector.English;

public class RecipeAdapterWithMissing extends Adapter<RecipeAdapterWithMissing.RecipeAdapterWithMissingViewHolder> {

    private final JSONArray nearlyThereRecipes;

    public RecipeAdapterWithMissing(JSONArray nearlyThereRecipes) {
        this.nearlyThereRecipes = nearlyThereRecipes;
    }

    @Override
    public RecipeAdapterWithMissing.RecipeAdapterWithMissingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout.item_recipe_with_missing, viewGroup, false);
        return new RecipeAdapterWithMissing.RecipeAdapterWithMissingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeAdapterWithMissing.RecipeAdapterWithMissingViewHolder viewHolder, int i) {
        JSONObject nearlyThereRecipe = this.nearlyThereRecipes.optJSONObject(i);
        JSONObject recipe = nearlyThereRecipe.optJSONObject(Api.NEARLY_THERE_RECIPE);
        int missing = nearlyThereRecipe.optInt(Api.NEARLY_THERE_MISSING_COUNT);

        viewHolder.name.setText(recipe.optString(Api.RECIPE_NAME));
        viewHolder.description.setText(recipe.optString(Api.RECIPE_DESCRIPTION));
        viewHolder.missing.setText(missing + " " + English.plural("ingredient", missing) + " missing");

        ImageLoader.getInstance().loadImage(recipe.optString(Api.RECIPE_BANNER), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                viewHolder.avatar.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.nearlyThereRecipes.length();
    }

    public static class RecipeAdapterWithMissingViewHolder extends ViewHolder {

        ImageView avatar;
        TextView name;
        TextView description;
        TextView missing;

        public RecipeAdapterWithMissingViewHolder(View itemView) {
            super(itemView);
            this.avatar = (ImageView) itemView.findViewById(id.avatar);
            this.name = (TextView) itemView.findViewById(id.primary_text);
            this.description = (TextView) itemView.findViewById(id.secondary_text);
            this.missing = (TextView) itemView.findViewById(id.missing_text);
        }
    }

}
