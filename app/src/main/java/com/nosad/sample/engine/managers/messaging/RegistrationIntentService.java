package com.nosad.sample.engine.managers.messaging;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.network.RequestCallback;
import com.nosad.sample.engine.network.RestManager;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

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

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String registrationId = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            registerDevice(deviceName, deviceId, registrationId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers device against application server.
     * If device hasn't been registered before, successful response is returned
     * with according message.
     * Otherwise error response is returned with appropriate message.
     *
     * @param deviceName - name of the registered device
     * @param deviceId - IMEI of the device
     * @param registrationId - registration id of the device
     */
    private void registerDevice(String deviceName, String deviceId, String registrationId) {
        JSONObject device = new JSONObject();
        try {
            device.put("deviceId", deviceId);
            device.put("deviceName", deviceName);
            device.put("registrationId", registrationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestManager.getInstance(getApplicationContext()).createRequest(Constants.REST_API_DEVICES, Request.Method.POST, device, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    Intent intent = new Intent(GCMManager.REGISTRATION_PROCESS);
                    intent.putExtra("result", response.getString("result"));
                    intent.putExtra("message", response.getString("message"));
                    App.getLocalBroadcastManager().sendBroadcast(intent);
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
