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

/**
 * Utility class that receives a string url and returns a JSONObject.
 */
public final class JSONGrabber {

    private URL url;
    private HttpURLConnection connection;

    public JSONGrabber(String url) throws IOException {
        this.url = new URL(url);
        this.setupConnection();
    }

    public void reuseConnection(String url) throws IOException {
        this.url = new URL(url);
        this.setupConnection();
    }

    private void setupConnection() throws IOException {
        this.connection = (HttpURLConnection) this.url.openConnection();
        this.connection.setRequestMethod(Constants.REQUEST_METHOD_GET);
        this.connection.setRequestProperty("Content-length", "0");
        this.connection.setUseCaches(false);
        this.connection.setAllowUserInteraction(false);
        this.connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
        this.connection.setReadTimeout(Constants.READ_TIMEOUT);
        this.connection.setDoInput(Constants.DO_INPUT);
    }

    public JSONObject grab() throws IOException, JSONException {
        InputStream is = null;
        JSONObject object = null;

        Log.d(Constants.APP_DEBUG, "Attempting to connect to " + this.url);
        this.connection.connect();

        Log.d(Constants.APP_DEBUG, "Connection to " + this.url + " established.");

        if (this.connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            String line;
            is = this.connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null)
                response.append(line);

            Log.d(Constants.APP_DEBUG, "Response: " + response);
            object = new JSONObject(response.toString());
        } else {
            Log.d(Constants.APP_DEBUG, "HTTP_CONNECTION: " + this.connection.getResponseCode());
        }
        if (is != null) {
            is.close();
        }
        if (this.connection != null) {
            this.connection.disconnect();
        }

        return object;
    }

}
