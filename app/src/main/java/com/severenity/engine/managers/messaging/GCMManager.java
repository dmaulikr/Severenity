package com.severenity.engine.managers.messaging;

import android.content.Context;

/**
 * Google cloud messaging manager.
 *
 * Created by Novosad on 5/8/16.
 */
public class GCMManager {
    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";
    public static final String QUEST_RECEIVED = "quest_received";
    public static final String LEVEL_UP_RECEIVED = "level_up_received";

    private Context context;

    public GCMManager(Context context) {
        this.context = context;
    }
}
