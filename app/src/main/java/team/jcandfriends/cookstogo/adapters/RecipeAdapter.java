package team.jcandfriends.cookstogo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.RecipeActivity;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private JSONArray recipes;
    private Activity activity;

    public RecipeAdapter(Activity activity, JSONArray recipes) {
        this.activity = activity;
        this.recipes = recipes;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        JSONObject obj = recipes.optJSONObject(position);
        holder.icon.setImageResource(R.drawable.circle);
        holder.name.setText(obj.optString(Api.RECIPE_NAME));
        holder.description.setText(obj.optString(Api.RECIPE_DESCRIPTION));
    }

    @Override
    public int getItemCount() {
        return recipes.length();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView icon;
        TextView name;
        TextView description;

        public RecipeViewHolder(View itemView, final Activity activity) {
            super(itemView);
            this.view = itemView;
            this.icon = (ImageView) itemView.findViewById(R.id.avatar);
            this.name = (TextView) itemView.findViewById(R.id.primary_text);
            this.description = (TextView) itemView.findViewById(R.id.secondary_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, RecipeActivity.class));
                }
            });
        }

    }

}