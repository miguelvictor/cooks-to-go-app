package team.jcandfriends.cookstogo.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import team.jcandfriends.cookstogo.JSONGrabber;
import team.jcandfriends.cookstogo.Utils;

/**
 * RecommendRecipesTask fetches recipe with the supplied list of ingredients, which is
 * acquired from the virtual basket, on the REST server backend asynchronously.
 * <p/>
 * Subordinates: FetchRecipeTask.Callbacks, JSONGrabber
 */
public class RecommendRecipesTask extends AsyncTask<String, Void, JSONObject> {

    private final RecommendRecipesTask.Callbacks callbacks;

    public RecommendRecipesTask(RecommendRecipesTask.Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public static void start(String url, RecommendRecipesTask.Callbacks callbacks) {
        RecommendRecipesTask task = new RecommendRecipesTask(callbacks);
        task.execute(url);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.callbacks.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            JSONGrabber recipeGrabber = new JSONGrabber(params[0]);
            return recipeGrabber.grab();
        } catch (IOException | JSONException e) {
            Utils.log(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject recipes) {
        super.onPostExecute(recipes);
        this.callbacks.onPostExecute(recipes);
    }

    /**
     * Interface definition for callbacks to be invoked when events happen during the execution of this task
     */
    public interface Callbacks {

        void onPreExecute();

        void onPostExecute(JSONObject recipes);

    }
}