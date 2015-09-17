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

/**
 * RecipeIngredientsAdapter is responsible for displaying all the ingredients used in a recipe.
 * <p/>
 * Subordinates: RecipeIngredientViewHolder, item_recipe_ingredient.xml
 */
public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.RecipeIngredientViewHolder> {

    private JSONArray recipeComponents;

    public RecipeIngredientsAdapter(JSONArray recipeComponents) {
        this.recipeComponents = recipeComponents;
    }

    @Override
    public RecipeIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_ingredient, parent, false);
        return new RecipeIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeIngredientViewHolder holder, int position) {
        final JSONObject recipeComponent = recipeComponents.optJSONObject(position);

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
        return recipeComponents.length();
    }

    public static class RecipeIngredientViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name;

        public RecipeIngredientViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.primary_text);

            itemView.setClickable(true);
        }
    }

}
