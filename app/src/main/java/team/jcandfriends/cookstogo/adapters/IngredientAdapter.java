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
 * IngredientAdapter is used to display all the ingredients in the RecyclerView.
 * <p/>
 * Subordinates: item_ingredient.xml, IngredientViewHolder
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private JSONArray ingredients;

    public IngredientAdapter(JSONArray ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IngredientViewHolder holder, int position) {
        JSONObject ingredient = ingredients.optJSONObject(position);

        holder.name.setText(ingredient.optString(Api.INGREDIENT_NAME));
        ImageLoader.getInstance().loadImage(ingredient.optString(Api.INGREDIENT_ICON), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.avatar.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.length();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.primary_text);

            itemView.setClickable(true);
        }
    }

}