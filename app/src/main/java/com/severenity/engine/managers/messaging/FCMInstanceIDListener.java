package com.severenity.engine.managers.messaging;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.severenity.App;

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
