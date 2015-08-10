package team.jcandfriends.cookstogo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new InitializerTask().execute(Constants.URL_RECIPES_ALL);
    }

    private class InitializerTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(Constants.REQUEST_METHOD_GET);
                con.setDoInput(true);
                con.setConnectTimeout(Constants.CONNECT_TIMEOUT);
                con.setReadTimeout(Constants.READ_TIMEOUT);
                con.connect();

                switch (con.getResponseCode()) {
                    case 200:
                    case 201:
                        StringBuilder response = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String line;
                        while ((line = br.readLine()) != null)
                            response.append(line);
                        Log.d(Constants.APP_DEBUG, response.toString());
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            startActivity(new Intent(SplashScreenActivity.this, RecipesActivity.class));
            finish();
        }
    }

}
