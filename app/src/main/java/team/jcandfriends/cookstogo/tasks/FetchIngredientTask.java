package team.jcandfriends.cookstogo.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.JSONGrabber;
import team.jcandfriends.cookstogo.Utils;

public class FetchIngredientTask extends AsyncTask<Integer, Void, JSONObject> {

    private Callbacks callbacks;

    private FetchIngredientTask(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public static void start(int ingredientId, FetchIngredientTask.Callbacks callbacks) {
        FetchIngredientTask task = new FetchIngredientTask(callbacks);
        task.execute(ingredientId);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callbacks.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {
        try {
            JSONGrabber ingredientGrabber = new JSONGrabber(Api.getIngredientUrl(params[0]));
            return ingredientGrabber.grab();
        } catch (IOException | JSONException e) {
            Utils.log("Exception : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject ingredient) {
        super.onPostExecute(ingredient);
        callbacks.onPostExecute(ingredient);
    }

    public interface Callbacks {

        void onPreExecute();

        void onPostExecute(JSONObject ingredient);

    }
}