package team.jcandfriends.cookstogo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import team.jcandfriends.cookstogo.R.id;
import team.jcandfriends.cookstogo.R.layout;
import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.RecipeManager;

/**
 * The activity that is shown while the app is loading its initial data
 */
public class SplashScreenActivity extends Activity {

    private RecipeManager mRecipeManager;
    private IngredientManager mIngredientManager;

    private TextView mOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_splash_screen);

        mRecipeManager = RecipeManager.get(this);
        mIngredientManager = IngredientManager.get(this);

        mOutput = (TextView) this.findViewById(id.output);

        if (mRecipeManager.hasCachedRecipeTypes() && mIngredientManager.hasCachedIngredientTypes()) {
            startActivity(new Intent(this, RecipesActivity.class));
            finish();
        } else if (Utils.hasInternet(this)) {
            initialize();
        } else {
            mOutput.setText("Sorry, CooksToGo needs internet. After a first successful connection, data will be cached and can be accessed without internet.");
        }
    }

    private void onPostExecute(JSONObject[] jsonObjects) throws JSONException {
        if (null != jsonObjects) {
            JSONArray temp = jsonObjects[0].getJSONArray(Api.RESULTS);
            mRecipeManager.cacheRecipeTypes(temp);

            temp = jsonObjects[1].getJSONArray(Api.RESULTS);
            mIngredientManager.cacheIngredientTypes(temp);

            Intent intent = new Intent(this, RecipesActivity.class);
            startActivity(intent);
            finish();
        } else {
            mOutput.setText("Oh my god! It failed, trying again...");
            initialize();
        }
    }

    private void initialize() {
        new AsyncTask<Void, Void, JSONObject[]>() {
            @Override
            protected JSONObject[] doInBackground(Void... params) {
                try {
                    JSONObject[] objects = new JSONObject[2];
                    JSONGrabber grabber = new JSONGrabber(Api.RECIPE_TYPES);
                    objects[0] = grabber.grab();
                    grabber.reuseConnection(Api.INGREDIENT_TYPES);
                    objects[1] = grabber.grab();
                    return objects;
                } catch (IOException | JSONException e) {
                    Utils.log("Exception while grabbing latest recipe and ingredient types.");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject[] jsonObjects) {
                super.onPostExecute(jsonObjects);
                try {
                    SplashScreenActivity.this.onPostExecute(jsonObjects);
                } catch (JSONException e) {
                    Utils.log("Cannot parse the response of the server. This should not happen.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }

}
