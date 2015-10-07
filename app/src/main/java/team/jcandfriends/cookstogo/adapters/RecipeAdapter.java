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
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;

/**
 * RecipeAdapter is used to display all the recipes in the RecyclerView.
 * <p/>
 * Subordinates: item_recipe.xml, RecipeViewHolder
 */
public class RecipeAdapter extends Adapter<RecipeAdapter.RecipeViewHolder> {

    private final JSONArray mRecipes;

    public RecipeAdapter(JSONArray recipes) {
        mRecipes = recipes;
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeAdapter.RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecipeAdapter.RecipeViewHolder holder, int position) {
        JSONObject obj = mRecipes.optJSONObject(position);

        ImageLoader.getInstance().loadImage(obj.optString(Api.RECIPE_ICON), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.icon.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });

        holder.name.setText(obj.optString(Api.RECIPE_NAME));
        holder.description.setText(obj.optString(Api.RECIPE_DESCRIPTION));
    }

    @Override
    public int getItemCount() {
        return mRecipes.length();
    }

    public static class RecipeViewHolder extends ViewHolder {

        View view;
        ImageView icon;
        TextView name;
        TextView description;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            icon = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.primary_text);
            description = (TextView) itemView.findViewById(R.id.secondary_text);
        }
    }

}