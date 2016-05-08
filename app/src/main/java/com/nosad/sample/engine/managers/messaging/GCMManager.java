package com.nosad.sample.engine.managers.messaging;

import android.content.Context;

/**
 * Google cloud messaging manager.
 *
 * Created by Novosad on 5/8/16.
 */
public class GCMManager {
    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";

    private Context context;

    public GCMManager(Context context) {
        this.context = context;
    }
}
