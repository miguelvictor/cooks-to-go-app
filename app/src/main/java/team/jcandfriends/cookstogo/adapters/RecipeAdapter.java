package team.jcandfriends.cookstogo.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.Constants;
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.RecipeActivity;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.tasks.FetchRecipeTask;


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
        return new RecipeViewHolder(itemView, activity, recipes);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, final int position) {
        final JSONObject obj = recipes.optJSONObject(position);

        ImageLoader.getInstance().loadImage(obj.optString(Api.RECIPE_ICON), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.icon.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });

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

        public RecipeViewHolder(View itemView, final Activity activity, final JSONArray recipes) {
            super(itemView);
            this.view = itemView;
            this.icon = (ImageView) itemView.findViewById(R.id.avatar);
            this.name = (TextView) itemView.findViewById(R.id.primary_text);
            this.description = (TextView) itemView.findViewById(R.id.secondary_text);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog progress = new AlertDialog.Builder(activity)
                            .setTitle("Loading Recipe")
                            .setMessage("Please wait while loading recipe ...")
                            .setCancelable(false)
                            .create();
                    final Intent intent = new Intent(activity, RecipeActivity.class);
                    final int recipeId = recipes.optJSONObject(getLayoutPosition()).optInt(Api.RECIPE_PK);
                    intent.putExtra(Constants.EXTRA_RECIPE_ID, recipeId);

                    if (Utils.hasInternet(activity)) {
                        progress.show();
                        FetchRecipeTask task = new FetchRecipeTask(new FetchRecipeTask.Callbacks() {
                            @Override
                            public void onPreExecute() {
                                progress.show();
                            }

                            @Override
                            public void onPostExecute(JSONObject recipe) {
                                if (recipe != null) {
                                    Data.cacheRecipe(activity, recipe);
                                }
                                progress.hide();
                                activity.startActivity(intent);
                            }
                        });
                        task.execute(recipeId);
                    } else if (Data.hasCachedRecipe(activity, recipeId)) {
                        activity.startActivity(intent);
                    } else {
                        new AlertDialog.Builder(activity)
                                .setTitle("No connection")
                                .setMessage("You need to connect to internet and open the recipe for us to cache the data so you can view it offline.")
                                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create().show();
                    }
                }
            });
        }
    }

}