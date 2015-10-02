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

/**
 * RecipeIngredientsAdapter is responsible for displaying all the ingredients used in a recipe.
 * <p/>
 * Subordinates: RecipeIngredientViewHolder, item_recipe_ingredient.xml
 */
public class RecipeIngredientsAdapter extends Adapter<RecipeIngredientsAdapter.RecipeIngredientViewHolder> {

    private final JSONArray recipeComponents;

    public RecipeIngredientsAdapter(JSONArray recipeComponents) {
        this.recipeComponents = recipeComponents;
    }

    @Override
    public RecipeIngredientsAdapter.RecipeIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.item_recipe_ingredient, parent, false);
        return new RecipeIngredientsAdapter.RecipeIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeIngredientsAdapter.RecipeIngredientViewHolder holder, int position) {
        JSONObject recipeComponent = this.recipeComponents.optJSONObject(position);

        holder.name.setText(Api.getIngredientReadableName(recipeComponent));

        ImageLoader.getInstance().loadImage(recipeComponent.optJSONObject(Api.RECIPE_COMPONENT_INGREDIENT).optString(Api.INGREDIENT_ICON), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (null != loadedImage) {
                    holder.avatar.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.recipeComponents.length();
    }

    public static class RecipeIngredientViewHolder extends ViewHolder {

        ImageView avatar;
        TextView name;

        public RecipeIngredientViewHolder(View itemView) {
            super(itemView);
            this.avatar = (ImageView) itemView.findViewById(id.avatar);
            this.name = (TextView) itemView.findViewById(id.primary_text);

            itemView.setClickable(true);
        }
    }

}
