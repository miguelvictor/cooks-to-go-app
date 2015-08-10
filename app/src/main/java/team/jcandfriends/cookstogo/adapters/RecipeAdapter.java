package team.jcandfriends.cookstogo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import team.jcandfriends.cookstogo.R;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private JSONArray recipes;

    public RecipeAdapter(JSONArray jsonArray) {
        this.recipes = jsonArray;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
//        try {
//            JSONObject recipe = recipes.getJSONObject(position);
//            holder.icon.setImageResource(R.drawable.circle);
//            holder.name.setText(recipe.getString("name"));
//            holder.description.setText(recipe.getString("description"));
//        } catch (JSONException e) {
//            Log.d(Constants.APP_DEBUG, "JSONException while binding data in position " + position);
//        }

        holder.icon.setImageResource(R.drawable.circle);
        holder.name.setText("Recipe " + position);
        holder.description.setText("Description sa recipe");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;
        TextView description;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.avatar);
            this.name = (TextView) itemView.findViewById(R.id.primary_text);
            this.description = (TextView) itemView.findViewById(R.id.secondary_text);
        }
    }

}