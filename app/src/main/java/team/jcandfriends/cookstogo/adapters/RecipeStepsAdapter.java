package team.jcandfriends.cookstogo.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;

/**
 * RecipeStepsAdapter is responsible for displaying all the steps in a recipe.
 * <p/>
 * Subordinates: RecipeStepViewHolder, item_recipe_step.xml
 */
public class RecipeStepsAdapter extends Adapter<RecipeStepsAdapter.RecipeStepViewHolder> {

    JSONArray steps;

    public RecipeStepsAdapter(JSONArray steps) {
        this.steps = steps;
    }

    @Override
    public RecipeStepsAdapter.RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.item_recipe_step, parent, false);
        return new RecipeStepsAdapter.RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepsAdapter.RecipeStepViewHolder holder, int position) {
        JSONObject step = this.steps.optJSONObject(position);
        holder.header.setText("Step " + step.optInt(Api.STEP_SEQUENCE));
        holder.text.setText(step.optString(Api.STEP_INSTRUCTION));
    }

    @Override
    public int getItemCount() {
        return this.steps.length();
    }


    public static class RecipeStepViewHolder extends ViewHolder {

        TextView header;
        TextView text;

        public RecipeStepViewHolder(View itemView) {
            super(itemView);

            this.header = (TextView) itemView.findViewById(id.step_header);
            this.text = (TextView) itemView.findViewById(id.step_text);
        }
    }
}
