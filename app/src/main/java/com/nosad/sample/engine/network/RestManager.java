package com.nosad.sample.engine.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nosad.sample.R;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONObject;

/**
 * Created by Oleg Novosad on 8/27/2015.
 *
 * Responsible for doing all the communication work with RESTful web services.
 * Using Volley for queueing requests and handling responses.
 */
public class RestManager {
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private Context context;

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

    /**
     * Private singleton constructor.
     *
     * @param context - context of the caller (activity, application etc.)
     */
    public RestManager(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    /**
     * Get request queue. If current is null - create new one based on application context.
     *
     * @return {@link RequestQueue} object
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Creates requests and adds it to the queue of the RestManager.
     *
     * @param url - url to make request to.
     * @param requestMethod - any of {@link com.android.volley.Request.Method} values.
     * @param data - json data to be sent to server.
     */
    public void createRequest(final String url, final int requestMethod, final JSONObject data, final RequestCallback callback) {
        // Request a string response from the url
        JsonObjectRequest request = new JsonObjectRequest(requestMethod, url, data,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    callback.onResponseCallback(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onErrorCallback(error.networkResponse);
                }
        });

        addToRequestQueue(request);
    }

    /**
     * Add newly created request to the queue.
     * Supports different requests (JSONObject, String etc.)
     *
     * @param request - request to be added to the queue.
     * @param <T> - type of request.
     */
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public NetworkReceiver getNetworkReceiver() {
        return new NetworkReceiver();
    }

    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            updateConnectedFlags();

            // Checks the user prefs and the network connection. Based on the result, decides whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if (networkInfo != null) {
                refreshDisplay = true;
                // If device has its Wi-Fi connection, sets refreshDisplay
                // to true. This causes the display to be refreshed when the user
                // returns to the app.
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.d(Constants.TAG, context.getResources().getString(R.string.wifi_connected));
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // If the setting is ANY network and there is a network connection
                    // (which by process of elimination would be mobile), sets refreshDisplay to true.
                    Log.d(Constants.TAG, context.getResources().getString(R.string.mobile_connected));
                } else {
                    Log.d(Constants.TAG, context.getResources().getString(R.string.connection_established));
                }
            } else {
                refreshDisplay = false;
                Log.d(Constants.TAG, context.getResources().getString(R.string.lost_connection));
            }
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    /**
     *
     * @return true if connect to mobile or wifi network.
     */
    public boolean isConnected() {
        return wifiConnected || mobileConnected;
    }
}
