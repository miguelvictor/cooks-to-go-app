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

import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.Utils;

/**
 * VirtualBasketItemsAdapter is responsible for displaying all the mItems in the virtual basket.
 * <p/>
 * Subordinates: VirtualBasketItemViewHolder, item_virtual_basket_item_item.xml
 */
public class VirtualBasketItemsAdapter extends Adapter<VirtualBasketItemsAdapter.VirtualBasketItemViewHolder> {

    private final ArrayList<JSONObject> mItems;

    public VirtualBasketItemsAdapter(ArrayList<JSONObject> items) {
        mItems = items;
    }

    @Override
    public VirtualBasketItemsAdapter.VirtualBasketItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.item_virtual_basket_item, parent, false);
        return new VirtualBasketItemsAdapter.VirtualBasketItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VirtualBasketItemsAdapter.VirtualBasketItemViewHolder holder, int position) {
        JSONObject obj = mItems.get(position);

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
        return mItems.size();
    }

    public void addItem(JSONObject ingredient) {
        mItems.add(ingredient);
        notifyItemInserted(mItems.size() - 1);
    }

    public JSONObject getItem(int position) {
        return mItems.get(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class VirtualBasketItemViewHolder extends ViewHolder {

        ImageView avatar;
        TextView ingredientName;

        public VirtualBasketItemViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(id.avatar);
            ingredientName = (TextView) itemView.findViewById(id.primary_text);
        }
    }

}