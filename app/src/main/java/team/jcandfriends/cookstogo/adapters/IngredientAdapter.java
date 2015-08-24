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
import team.jcandfriends.cookstogo.Data;
import team.jcandfriends.cookstogo.IngredientActivity;
import team.jcandfriends.cookstogo.R;
import team.jcandfriends.cookstogo.Utils;
import team.jcandfriends.cookstogo.tasks.FetchIngredientTask;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private Activity activity;
    private JSONArray ingredients;

    public IngredientAdapter(Activity activity, JSONArray ingredients) {
        this.activity = activity;
        this.ingredients = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view, activity, ingredients);
    }

    @Override
    public void onBindViewHolder(final IngredientViewHolder holder, int position) {
        JSONObject ingredient = ingredients.optJSONObject(position);

        holder.name.setText(ingredient.optString(Api.INGREDIENT_NAME));
        ImageLoader.getInstance().loadImage(ingredient.optString(Api.INGREDIENT_ICON), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.avatar.setImageBitmap(Utils.getRoundedBitmap(loadedImage));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.length();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name;

        public IngredientViewHolder(View itemView, final Activity activity, final JSONArray ingredients) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.primary_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog progress = new AlertDialog.Builder(activity)
                            .setTitle(activity.getString(R.string.dialog_ingredient_loading_header))
                            .setMessage(activity.getString(R.string.dialog_ingredient_loading_subheader))
                            .setCancelable(false)
                            .create();

                    final Intent intent = new Intent(activity, IngredientActivity.class);
                    JSONObject ingredient = ingredients.optJSONObject(getLayoutPosition());
                    int ingredientId = ingredient.optInt(Api.INGREDIENT_PK);

                    intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_PK, ingredientId);
                    intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_NAME, ingredient.optString(Api.INGREDIENT_NAME));

                    if (Utils.hasInternet(activity)) {
                        progress.show();
                        FetchIngredientTask task = new FetchIngredientTask(new FetchIngredientTask.Callbacks() {
                            @Override
                            public void onPreExecute() {
                                progress.show();
                            }

                            @Override
                            public void onPostExecute(JSONObject ingredient) {
                                if (ingredient != null) {
                                    Data.cacheIngredient(activity, ingredient);
                                }
                                progress.hide();
                                activity.startActivity(intent);
                            }
                        });
                        task.execute(ingredientId);
                    } else if (Data.hasCachedIngredient(activity, ingredientId)) {
                        activity.startActivity(intent);
                    } else {
                        new AlertDialog.Builder(activity)
                                .setTitle(activity.getString(R.string.dialog_no_internet_header))
                                .setMessage(activity.getString(R.string.dialog_no_internet_subheader))
                                .setNeutralButton(activity.getString(R.string.dialog_neutral_button_label), new DialogInterface.OnClickListener() {
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