package com.nosad.sample.network;

import com.android.volley.NetworkResponse;

import org.json.JSONObject;

/**
 * Created by Oleg Novosad on 8/27/2015.
 *
 * Callback to be used after REST request was created and proceeded.
 */
public interface RequestCallback {
    void onResponseCallback(JSONObject response);
    void onErrorCallback(NetworkResponse response);
}
