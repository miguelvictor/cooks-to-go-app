package team.jcandfriends.cookstogo.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.VirtualBasketContainer;

public class VirtualBasketItemsAdapter extends RecyclerView.Adapter<VirtualBasketItemsAdapter.VirtualBasketItemViewHolder> {

    private JSONArray items;

    public VirtualBasketItemsAdapter(Activity activity) {
        VirtualBasketContainer.initialize(activity);
        items = VirtualBasketContainer.getData();
    }

    @Override
    public VirtualBasketItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_virtual_basket, parent, false);
        return new VirtualBasketItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VirtualBasketItemViewHolder holder, int position) {
        try {
            JSONObject obj = items.getJSONObject(position);
            holder.ingredientName.setText(
                    obj.getString(VirtualBasketContainer.VIRTUAL_INGREDIENT)
            );
            holder.ingredientDescription.setText(
                    obj.getInt(VirtualBasketContainer.VIRTUAL_QUANTITY) + " " +
                            obj.getString(VirtualBasketContainer.VIRTUAL_UNIT_OF_MEASURE) + " of " +
                            obj.getString(VirtualBasketContainer.VIRTUAL_INGREDIENT)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.length();
    }

    public static class VirtualBasketItemViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView ingredientName;
        TextView ingredientDescription;

        public VirtualBasketItemViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            ingredientName = (TextView) itemView.findViewById(R.id.primary_text);
            ingredientDescription = (TextView) itemView.findViewById(R.id.secondary_text);
        }
    }

}