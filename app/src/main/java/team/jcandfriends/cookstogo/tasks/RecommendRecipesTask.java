package team.jcandfriends.cookstogo.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.JSONGrabber;
import team.jcandfriends.cookstogo.Utils;

/**
 * RecommendRecipesTask fetches recipe with the supplied list of ingredients, which is
 * acquired from the virtual basket, on the REST server backend asynchronously.
 * <p/>
 * Subordinates: FetchRecipeTask.Callbacks, JSONGrabber
 */
public class RecommendRecipesTask extends AsyncTask<Integer, Void, JSONObject> {

    private Callbacks callbacks;

    private RecommendRecipesTask(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public static void start(int recipeId, Callbacks callbacks) {
        RecommendRecipesTask task = new RecommendRecipesTask(callbacks);
        task.execute(recipeId);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callbacks.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {
        try {
            JSONGrabber recipeGrabber = new JSONGrabber(Api.getRecipeUrl(params[0]));
            return recipeGrabber.grab();
        } catch (IOException | JSONException e) {
            Utils.log(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject recipe) {
        super.onPostExecute(recipe);
        callbacks.onPostExecute(recipe);
    }

    /**
     * Interface definition for callbacks to be invoked when events happen during the execution of this task
     */
    public interface Callbacks {

        void onPreExecute();

        void onPostExecute(JSONObject recipes);

    }
}