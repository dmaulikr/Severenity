package com.severenity.engine.managers.messaging;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.severenity.App;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.User;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class is responsible for registration of the device against GCM server.
 *
 * Created by Novosad on 5/6/16.
 */
public class RegistrationIntentService extends IntentService {
    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String deviceId = intent.getStringExtra(Constants.INTENT_EXTRA_DEVICE_ID);
        String deviceName = intent.getStringExtra(Constants.INTENT_EXTRA_DEVICE_NAME);
        String userId = intent.getStringExtra(Constants.INTENT_EXTRA_USER_ID);
        String registrationId = intent.getStringExtra(Constants.INTENT_EXTRA_REGISTRATION_ID);

        registerDevice(userId, deviceName, deviceId, registrationId);
    }

    /**
     * Registers device against application server.
     * If device hasn't been registered before, successful response is returned
     * with according message.
     * Otherwise error response is returned with appropriate message.
     *
     * @param userId - id of the user to register device to.
     * @param deviceName - name of the registered device.
     * @param deviceId - IMEI of the device.
     * @param registrationId - registration id of the device.
     */
    private void registerDevice(String userId, String deviceName, String deviceId, String registrationId) {
        JSONObject device = new JSONObject();
        try {
            device.put("deviceId", deviceId);
            device.put("deviceName", deviceName);
            device.put("registrationId", registrationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        App.getRestManager().createRequest(Constants.REST_API_USERS + "/" + userId + Constants.REST_API_DEVICES, Request.Method.POST, device, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    Intent intent = new Intent(Constants.INTENT_FILTER_AUTHENTICATION);
                    intent.putExtra("result", response.getString("result"));

                    JSONObject userObject = response.getJSONObject("data");

                    App.getUserManager().createCurrentUserAndNotify(userObject, intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                if (response != null && response.data != null) {
                    Log.e(Constants.TAG, new String(response.data));
                    Toast.makeText(getApplicationContext(), "Error occurred on server.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), Constants.REST_API_DEVICES + " response is null.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
