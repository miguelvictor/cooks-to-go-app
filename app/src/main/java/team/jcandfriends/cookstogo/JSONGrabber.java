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

    private URL mUrl;
    private HttpURLConnection mConnection;

    public JSONGrabber(String url) throws IOException {
        mUrl = new URL(url);
        setupConnection();
    }

    public void reuseConnection(String url) throws IOException {
        mUrl = new URL(url);
        setupConnection();
    }

    private void setupConnection() throws IOException {
        mConnection = (HttpURLConnection) mUrl.openConnection();
        mConnection.setRequestMethod(Constants.REQUEST_METHOD_GET);
        mConnection.setRequestProperty("Content-length", "0");
        mConnection.setUseCaches(false);
        mConnection.setAllowUserInteraction(false);
        mConnection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
        mConnection.setReadTimeout(Constants.READ_TIMEOUT);
        mConnection.setDoInput(Constants.DO_INPUT);
    }

    public JSONObject grab() throws IOException, JSONException {
        InputStream is = null;
        JSONObject object = null;

        Log.d(Constants.APP_DEBUG, "Attempting to connect to " + mUrl);
        mConnection.connect();

        Log.d(Constants.APP_DEBUG, "Connection to " + mUrl + " established.");

        if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            String line;
            is = mConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null)
                response.append(line);

            Log.d(Constants.APP_DEBUG, "Response: " + response);
            object = new JSONObject(response.toString());
        } else {
            Log.d(Constants.APP_DEBUG, "HTTP_CONNECTION: " + mConnection.getResponseCode());
        }
        if (is != null) {
            is.close();
        }
        if (mConnection != null) {
            mConnection.disconnect();
        }

        return object;
    }

}
