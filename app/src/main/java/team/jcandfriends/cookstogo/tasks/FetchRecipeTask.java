package team.jcandfriends.cookstogo.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import team.jcandfriends.cookstogo.Api;
import team.jcandfriends.cookstogo.JSONGrabber;
import team.jcandfriends.cookstogo.Utils;

public class FetchRecipeTask extends AsyncTask<Integer, Void, JSONObject> {

    private Callbacks callbacks;

    public FetchRecipeTask(Callbacks callbacks) {
        this.callbacks = callbacks;
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

    public interface Callbacks {

        void onPreExecute();

        void onPostExecute(JSONObject recipe);

    }
}