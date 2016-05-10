package com.nosad.sample.utils.common;

import java.util.Locale;

/**
 * Created by Novosad on 02/06/2015.
 */
public class Constants {
    public enum Characteristic {
        Level("level"),
        Immunity("immunity"),
        Mentality("mentality"),
        Experience("experience");

        final String value;

        Characteristic(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public final static String TAG = "SAMPLE";

    public static final String GCM_SENDER_ID = "967925944048";

    public static final String INTENT_FILTER_GAC = "com.nosad.sample.googleapiclient";
    public static final String INTENT_FILTER_STEPS = "com.nosad.sample.steps";
    public static final String INTENT_FILTER_WARDS_COUNT = "com.nosad.sample.wardscount";
    public static final String INTENT_FILTER_UPDATE_UI = "com.nosad.sample.updateui";
    public static final String INTENT_FILTER_NEW_MESSAGE = "com.nosad.sample.newmessage";
    public static final String INTENT_FILTER_NEW_QUEST = "com.nosad.sample.newquest";

    public static final String INTENT_EXTRA_DEVICE_ID = "DEVICE_ID";
    public static final String INTENT_EXTRA_DEVICE_NAME = "DEVICE_NAME";

    public static final String EXTRA_GAC_CONNECTED = "isConnected";
    public static final String EXTRA_STEPS = "steps";

    public static final int EXPERIENCE_MULTIPLIER = 10;
    public static final int LEVEL_MULTIPLIER = 1000;

    public static final float RESTORE_RATE_MENTALITY_NORMAL = 0.1f; // per day
    public static final float RESTORE_RATE_MENTALITY_MOVING = 1f;   // per 100 meters

    public static final double MINIMUM_DISTANCE_FOR_UPDATE = 100;   // in meters
    public final static String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public final static Locale LOCALE = Locale.US;

    /**
     * Connection constants
     */

    public final static String HOST =  "https://severenity.herokuapp.com";
//    public final static String HOST =  "http://localhost:8080";
    private final static String API_ADDRESS = HOST + "/api";

    public final static int CONNECTION_READ_TIMEOUT = 10000; // milliseconds
    public final static int CONNECTION_CONNECT_TIMEOUT = 15000; // milliseconds

    public final static String SOCKET_EVENT_LOCATION = "location";
    public final static String SOCKET_EVENT_MESSAGE = "chat message";

    /**
     * RESTful APIs constants
     */
    public static String REST_API_USERS = HOST + "/users";
    public static String REST_API_DEVICES = HOST + "/devices";
    public static String REST_API_SEND = HOST + "/send";

    /**
     * Exception constants
     */
    public static String EXCEPTION_FB_ACCESS_TOKEN_MISSING = "Facebook access token missing.";

    /**
     * Shared preferences constants
     */

    public static String PREFS_DEVICE_REGISTERED = "deviceRegistered";
}
