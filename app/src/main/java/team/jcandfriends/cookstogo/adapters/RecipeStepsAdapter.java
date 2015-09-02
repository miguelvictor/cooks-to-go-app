package team.jcandfriends.cookstogo.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.R;

/**
 * RecipeStepsAdapter is responsible for displaying all the steps in a recipe.
 * <p/>
 * Subordinates: RecipeStepViewHolder, item_recipe_step.xml
 */
public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepViewHolder> {

    private JSONArray steps;

    public RecipeStepsAdapter(JSONArray steps) {
        this.steps = steps;
    }

    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_step, parent, false);
        return new RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {
        try {
            JSONObject step = steps.getJSONObject(position);
            holder.header.setText("Step " + step.optInt(Api.STEP_SEQUENCE));
            holder.text.setText(step.optString(Api.STEP_INSTRUCTION));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return steps.length();
    }


    public static class RecipeStepViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        TextView text;

        public RecipeStepViewHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView.findViewById(R.id.step_header);
            text = (TextView) itemView.findViewById(R.id.step_text);
        }
    }
}
