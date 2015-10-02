package team.jcandfriends.cookstogo.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.JSONGrabber;
import team.jcandfriends.cookstogo.Utils;

/**
 * FetchRecipeTask fetches recipe on the REST server backend asynchronously.
 * <p/>
 * Subordinates: FetchRecipeTask.Callbacks, JSONGrabber
 */
public class FetchRecipeTask extends AsyncTask<Integer, Void, JSONObject> {

    private final FetchRecipeTask.Callbacks callbacks;

    private FetchRecipeTask(FetchRecipeTask.Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public static void start(int recipeId, FetchRecipeTask.Callbacks callbacks) {
        FetchRecipeTask task = new FetchRecipeTask(callbacks);
        task.execute(recipeId);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.callbacks.onPreExecute();
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
        this.callbacks.onPostExecute(recipe);
    }

    /**
     * Interface definition for callbacks to be invoked when events happen during the execution of this task
     */
    public interface Callbacks {

        void onPreExecute();
        void onPostExecute(JSONObject recipe);

    }
}