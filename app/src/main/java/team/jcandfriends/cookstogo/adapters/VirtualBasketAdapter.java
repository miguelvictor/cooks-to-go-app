package team.jcandfriends.cookstogo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.managers.VirtualBasketManager;

public class VirtualBasketAdapter extends RecyclerView.Adapter<VirtualBasketAdapter.VirtualBasketViewHolder> {

    private ArrayList<JSONObject> mVirtualBaskets;

    public VirtualBasketAdapter(ArrayList<JSONObject> virtualBaskets) {
        mVirtualBaskets = virtualBaskets;
    }

    @Override
    public VirtualBasketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_virtual_basket, parent, false);
        return new VirtualBasketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VirtualBasketViewHolder holder, int position) {
        JSONObject virtualBasket = mVirtualBaskets.get(position);
        holder.name.setText(virtualBasket.optString(VirtualBasketManager.VIRTUAL_BASKET_NAME));
    }

    @Override
    public int getItemCount() {
        return mVirtualBaskets.size();
    }

    public static class VirtualBasketViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public VirtualBasketViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.virtual_basket_name);
        }
    }

}
