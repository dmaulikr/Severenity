package com.severenity.engine.network;

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
import com.severenity.R;
import com.severenity.utils.common.Constants;

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
                    Log.i(Constants.TAG, response.toString());
                    callback.onResponseCallback(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(Constants.TAG, error.toString());
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
}
