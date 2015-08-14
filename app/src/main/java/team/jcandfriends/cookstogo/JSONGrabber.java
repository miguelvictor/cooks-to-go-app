package team.jcandfriends.cookstogo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONGrabber {

    private URL url;
    private HttpURLConnection connection;

    public JSONGrabber(String url) throws IOException {
        this.url = new URL(url);
        setupConnection();
    }

    public void reuseConnection(String url) throws IOException {
        this.url = new URL(url);
        setupConnection();
    }

    private void setupConnection() throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(Constants.REQUEST_METHOD_GET);
        connection.setRequestProperty("Content-length", "0");
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
        connection.setReadTimeout(Constants.READ_TIMEOUT);
        connection.setDoInput(Constants.DO_INPUT);
    }

    public JSONObject grab() throws IOException, JSONException {
        InputStream is = null;
        JSONObject object = null;

        Log.d(Constants.APP_DEBUG, "Attempting to connect to " + url);
        connection.connect();

        Log.d(Constants.APP_DEBUG, "Connection to " + url + " established.");
        StringBuilder response = new StringBuilder();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String line;
            is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null)
                response.append(line);

            Log.d(Constants.APP_DEBUG, "Response: " + response);
            object = new JSONObject(response.toString());
        } else {
            Log.d(Constants.APP_DEBUG, "HTTP_CONNECTION: " + connection.getResponseCode());
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            connection.disconnect();
        }

        return object;
    }

}
