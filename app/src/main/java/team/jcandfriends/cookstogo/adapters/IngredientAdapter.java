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
 * IngredientAdapter is used to display all the mIngredients in the RecyclerView.
 * <p/>
 * Subordinates: item_ingredient.xml, IngredientViewHolder
 */
public class IngredientAdapter extends Adapter<IngredientAdapter.IngredientViewHolder> {

    private final JSONArray mIngredients;

    public IngredientAdapter(JSONArray mIngredients) {
        this.mIngredients = mIngredients;
    }

    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientAdapter.IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IngredientAdapter.IngredientViewHolder holder, int position) {
        JSONObject ingredient = mIngredients.optJSONObject(position);

        holder.mName.setText(ingredient.optString(Api.INGREDIENT_NAME));
        ImageLoader.getInstance().loadImage(ingredient.optString(Api.INGREDIENT_ICON), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.mAvatar.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIngredients.length();
    }

    public static class IngredientViewHolder extends ViewHolder {

        ImageView mAvatar;
        TextView mName;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.primary_text);

            itemView.setClickable(true);
        }
    }

}