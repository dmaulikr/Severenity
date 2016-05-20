package com.nosad.sample.utils;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

/**
 * Created by Novosad on 5/20/16.
 */
public class FacebookUtils {
    public interface Callback {
        void onResponse(GraphResponse response);
    }

    public static void getFacebookUserById(String id, String fields, final Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", fields);

        new GraphRequest(AccessToken.getCurrentAccessToken(), id, params, HttpMethod.GET,
            new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    callback.onResponse(response);
                }
        }).executeAsync();
    }
}
