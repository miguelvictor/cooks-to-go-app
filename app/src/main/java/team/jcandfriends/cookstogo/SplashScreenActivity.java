package team.jcandfriends.cookstogo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * The activity that is shown while the app is loading its initial data
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Utils.hasInternet(this)) {
            Utils.log("Internet available: Fetching latest Recipe and Ingredient types");
            new InitializerTask().execute();
        } else {
            Utils.log("Internet unavailable: Displaying cached data");
            if (Data.isSafeToProceed(this)) {
                startActivity(new Intent(SplashScreenActivity.this, RecipesActivity.class));
                finish();
            } else {
                ((TextView) findViewById(R.id.output)).setText("Sorry, CooksToGo needs internet.");
            }
        }
    }

    private class InitializerTask extends AsyncTask<Void, Void, JSONObject[]> {

        @Override
        protected JSONObject[] doInBackground(Void... params) {
            try {
                JSONObject[] objects = new JSONObject[2];
                JSONGrabber grabber = new JSONGrabber(Api.RECIPE_TYPES);
                objects[0] = grabber.grab();
                grabber.reuseConnection(Api.INGREDIENT_TYPES);
                objects[1] = grabber.grab();
                return objects;
            } catch (IOException e) {
                Utils.log("IOException while grabbing latest recipe types.");
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject[] jsonObject) {
            super.onPostExecute(jsonObject);

            if (null != jsonObject) {
                try {
                    Data.initializeRecipeTypes(SplashScreenActivity.this, jsonObject[0]);
                    Data.initializeIngredientTypes(SplashScreenActivity.this, jsonObject[1]);
                    startActivity(new Intent(SplashScreenActivity.this, RecipesActivity.class));
                    finish();
                } catch (JSONException e) {
                    Utils.log("JSONException while initializing data on the Data singleton.");
                    e.printStackTrace();
                }
            } else {
                Utils.log("JSONObject is null. Finishing activity ...");
                finish();
            }
        }
    }

}
