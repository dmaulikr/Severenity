package com.nosad.sample.engine.managers.messaging;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nosad.sample.App;
import com.nosad.sample.utils.common.Constants;

/**
 * Called if InstanceID token is updated. This may occur if the security of
 * the previous token had been compromised. Note that this is also called
 * when the InstanceID token is initially generated, so this is where
 * you retrieve the token.
 *
 * Created by Novosad on 5/4/16.
 */
public class FCMInstanceIDListener extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        App.setCurrentFCMToken(FirebaseInstanceId.getInstance().getToken());
    }
}
