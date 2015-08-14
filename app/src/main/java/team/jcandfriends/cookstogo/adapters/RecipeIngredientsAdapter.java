package team.jcandfriends.cookstogo.adapters;

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

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientOnRecipeViewHolder> {

    private JSONArray recipeComponents;

    public RecipeIngredientsAdapter(JSONArray recipeComponents) {
        this.recipeComponents = recipeComponents;
    }

    @Override
    public IngredientOnRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_on_recipe, parent, false);
        return new IngredientOnRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientOnRecipeViewHolder holder, int position) {
        final JSONObject ingredient = recipeComponents.optJSONObject(position);
        final StringBuilder name = new StringBuilder();
        name.append(ingredient.optInt(Api.RECIPE_COMPONENT_QUANTITY))
                .append(" ")
                .append(ingredient.optJSONObject(Api.RECIPE_COMPONENT_UNIT_OF_MEASURE).optString(Api.UNIT_OF_MEASURE_NAME))
                .append(" of ")
                .append(ingredient.optJSONObject(Api.RECIPE_COMPONENT_INGREDIENT).optString(Api.INGREDIENT_NAME).toLowerCase());

        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return recipeComponents.length();
    }

    public static class IngredientOnRecipeViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name;

        public IngredientOnRecipeViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.primary_text);
        }
    }

}
