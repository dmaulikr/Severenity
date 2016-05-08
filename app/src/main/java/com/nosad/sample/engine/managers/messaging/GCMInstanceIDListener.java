package com.nosad.sample.engine.managers.messaging;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Class is responsible for token refresh for current device.
 *
 * Created by Novosad on 5/4/16.
 */
public class GCMInstanceIDListener extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
