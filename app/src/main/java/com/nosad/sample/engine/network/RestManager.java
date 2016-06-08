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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.entity.GamePlace;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    /**
     * Getter for network changes receiver.
     *
     * @return {@link NetworkReceiver} - receiver of network changes.
     */
    public NetworkReceiver getNetworkReceiver() {
        return new NetworkReceiver();
    }

    /**
     * Receives updates for the network changes.
     * Checks for both mobile / WiFi networks.
     */
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

    /**
     * Checks the network connection and sets the wifiConnected and mobileConnected
     * variables accordingly.
     */
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

    /**
     * Sends API request to store place on the server DB.
     *
     * @param place - place to store on the server.
     */
    public void sendPlaceToServer(GamePlace place, RequestCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("placeId", place.getPlaceID());
            data.put("name", place.getPlaceName());
            data.put("lng", String.valueOf(place.getPlacePos().longitude));
            data.put("lat", String.valueOf(place.getPlacePos().latitude));

            App.getRestManager().createRequest(Constants.REST_API_PLACES, Request.Method.POST, data, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves user from the server.
     *
     * @param userId - id of the user to retrieve
     */
    public void getUser(String userId, RequestCallback callback) {
        createRequest(Constants.REST_API_USERS + "/" + userId, Request.Method.GET, null, callback);
    }

    /**
     * Authorize user against the server.
     * If user does not exist - server will create a new one with Facebook ID provided.
     *
     * @param userId - id of the user to authorize or create.
     */
    public void authorizeUser(String userId, RequestCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            createRequest(Constants.REST_API_USERS, Request.Method.POST, data, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve places from server based on current position and radius.
     *
     * @param currentPosition - current location of the user.
     * @param radius - radius to check places within.
     * @param callback - callback to execute after places were retrieved.
     */
    public void getPlacesFromServer (LatLng currentPosition, int radius, RequestCallback callback) {
        String request = Constants.REST_API_PLACES + "/?lng=" + Double.toString(currentPosition.longitude) + "&lat=" + Double.toString(currentPosition.latitude) + "&radius=" + Integer.toString(radius);
        App.getRestManager().createRequest(request, Request.Method.GET, null, callback);
    }

    /**
     * Sends a request to create a speicified {@link User} on the server.
     *
     * @param user - user to create on the server.
     * @param callback - callback to execute with response.
     */
    public void createUser(User user, RequestCallback callback) {
        JSONObject userObject = new JSONObject();
        try {
            userObject.put("userId", user.getId());
            userObject.put("name", user.getName());
            userObject.put("email", user.getEmail());

            App.getRestManager().createRequest(Constants.REST_API_CREATE_USER, Request.Method.POST, userObject, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
