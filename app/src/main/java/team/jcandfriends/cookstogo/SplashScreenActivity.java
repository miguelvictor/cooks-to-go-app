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

import team.jcandfriends.cookstogo.managers.IngredientManager;
import team.jcandfriends.cookstogo.managers.RecipeManager;

/**
 * The activity that is shown while the app is loading its initial data
 */
public class SplashScreenActivity extends Activity {

    RecipeManager recipeManager;
    IngredientManager ingredientManager;

    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        recipeManager = RecipeManager.get(this);
        ingredientManager = IngredientManager.get(this);

        output = (TextView) findViewById(R.id.output);

        if (Utils.hasInternet(this)) {
            Utils.log("Internet available: Fetching latest Recipe and Ingredient types");
            initialize();
        } else {
            Utils.log("Internet unavailable: Displaying cached data");
            if (recipeManager.hasCachedRecipeTypes() && ingredientManager.hasCachedIngredientTypes()) {
                startActivity(new Intent(SplashScreenActivity.this, RecipesActivity.class));
                finish();
            } else {
                output.setText("Sorry, CooksToGo needs internet. After a first successful connection, data will be cached and can be accessed without internet.");
            }
        }
    }

    private void onPostExecute(JSONObject[] jsonObjects) throws JSONException {
        if (null != jsonObjects) {
            JSONArray temp = jsonObjects[0].getJSONArray(Api.RESULTS);
            recipeManager.cacheRecipeTypes(temp);

            temp = jsonObjects[1].getJSONArray(Api.RESULTS);
            ingredientManager.cacheIngredientTypes(temp);

            Intent intent = new Intent(this, RecipesActivity.class);
            startActivity(intent);
            finish();
        } else {
            output.setText("Oh my god! It failed, trying again...");
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
                    Utils.log("Chaka na, di maparse ang response sa recipe ug ingredient types.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }

}
