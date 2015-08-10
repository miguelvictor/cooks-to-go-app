package team.jcandfriends.cookstogo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import team.jcandfriends.cookstogo.R;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        holder.avatar.setImageResource(R.drawable.circle);
        holder.name.setText("Ingredient " + position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name;

        public IngredientViewHolder(View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.primary_text);
        }
    }

}
