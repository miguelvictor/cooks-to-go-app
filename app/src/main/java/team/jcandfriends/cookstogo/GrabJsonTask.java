package team.jcandfriends.cookstogo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GrabJsonTask extends AsyncTask<URL, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(URL... params) {
        InputStream is = null;
        JSONObject jsonObject = null;

        try {
            HttpURLConnection con = (HttpURLConnection) params[0].openConnection();
            con.setRequestMethod(Constants.REQUEST_METHOD_GET);
            con.setRequestProperty("Content-length", "0");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.setConnectTimeout(Constants.CONNECT_TIMEOUT);
            con.setReadTimeout(Constants.READ_TIMEOUT);
            con.setDoInput(true);

            Log.d(Constants.APP_DEBUG, "Attempting to connect to " + params[0]);
            con.connect();

            Log.d(Constants.APP_DEBUG, "Connection established." + params[0]);
            StringBuilder response = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String line;
                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                while ((line = br.readLine()) != null)
                    response.append(line);

                Log.d(Constants.APP_DEBUG, "Response: " + response);
                jsonObject = new JSONObject(response.toString());
            } else {
                Log.d(Constants.APP_DEBUG, "HTTP_CONNECTION: " + con.getResponseCode());
            }
        } catch (IOException | JSONException e) {
            Log.d(Constants.APP_DEBUG, "Caught exception : " + e.getMessage());
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonObject;
    }

}
