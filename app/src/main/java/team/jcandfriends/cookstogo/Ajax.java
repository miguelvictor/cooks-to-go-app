package team.jcandfriends.cookstogo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public final class Ajax {

    public static final String STATUS_CODE = "status_code";
    public static final String RESPONSE_BODY = "response_body";
    private static final String TAG = "Ajax";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static int readTimeout = 0;
    private static int connectTimeout = 0;

    public static void get(String url, final Callbacks callbacks) {
        get(url, null, callbacks);
    }

    public static void get(String url, HashMap<String, String> queryParams, final Callbacks callbacks) {
        if (queryParams != null) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;

            for (String key : queryParams.keySet()) {
                try {
                    if (first) {
                        first = false;
                    } else {
                        builder.append("&");
                    }
                    builder
                            .append(URLEncoder.encode(key, DEFAULT_CHARSET))
                            .append("=")
                            .append(URLEncoder.encode(queryParams.get(key), DEFAULT_CHARSET));
                } catch (UnsupportedEncodingException ignore) {
                    // should never happen
                }
            }

            url += "?" + builder.toString();
        }
        new AsyncTask<String, Void, Bundle>() {
            @Override
            protected Bundle doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(readTimeout);
                    connection.setConnectTimeout(connectTimeout);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.connect();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putInt(STATUS_CODE, connection.getResponseCode());
                    bundle.putString(RESPONSE_BODY, builder.toString());
                    return bundle;
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Invalid URL", e);
                } catch (IOException e) {
                    Log.e(TAG, "Error while opening connection from url", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                super.onPostExecute(bundle);
                if (bundle != null) {
                    int statusCode = bundle.getInt(STATUS_CODE);
                    if (statusCode < 200 || statusCode >= 400) {
                        callbacks.onSuccess(statusCode, bundle.getString(RESPONSE_BODY));
                    } else {
                        callbacks.onFailure(statusCode, bundle.getString(RESPONSE_BODY));
                    }
                }
            }
        }.execute(url);
    }

    public void post(String url, HashMap<String, String> params, Callbacks callbacks) {
        if (params == null) {
            throw new NullPointerException("Call to Ajax.post() with null params");
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (String key : params.keySet()) {
            try {
                if (first) {
                    first = false;
                    builder.append(URLEncoder.encode(key, DEFAULT_CHARSET));
                    builder.append("=");
                    builder.append(URLEncoder.encode(params.get(key), DEFAULT_CHARSET));
                } else {

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Callbacks {
        void onSuccess(int statusCode, String responseBody);

        void onFailure(int statusCode, String responseBody);
    }

}
