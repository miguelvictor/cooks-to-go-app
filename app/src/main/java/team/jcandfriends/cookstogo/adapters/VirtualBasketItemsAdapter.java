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

import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;

/**
 * VirtualBasketItemsAdapter is responsible for displaying all the items in the virtual basket.
 * <p/>
 * Subordinates: VirtualBasketItemViewHolder, item_virtual_basket.xml
 */
public class VirtualBasketItemsAdapter extends RecyclerView.Adapter<VirtualBasketItemsAdapter.VirtualBasketItemViewHolder> {

    private ArrayList<JSONObject> items;

    public VirtualBasketItemsAdapter(ArrayList<JSONObject> items) {
        this.items = items;
    }

    @Override
    public VirtualBasketItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_virtual_basket, parent, false);
        return new VirtualBasketItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VirtualBasketItemViewHolder holder, int position) {
        JSONObject obj = items.get(position);

        String ingredientName = obj.optString(Api.INGREDIENT_NAME);
        holder.ingredientName.setText(ingredientName);

        ImageLoader.getInstance().loadImage(obj.optString(Api.INGREDIENT_ICON), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.avatar.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void deleteAll() {
        items.clear();
    }

    public void removeItem(JSONObject ingredient) {
        items.remove(ingredient);
    }

    public static class VirtualBasketItemViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView ingredientName;

        public VirtualBasketItemViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            ingredientName = (TextView) itemView.findViewById(R.id.primary_text);
        }
    }

}