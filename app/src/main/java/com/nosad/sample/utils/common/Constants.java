package com.nosad.sample.utils.common;

import java.util.Locale;

/**
 * Created by Novosad on 02/06/2015.
 */
public class Constants {
    public final static String TAG = "SAMPLE";

    public static final String INTENT_FILTER_GAC = "com.nosad.sample.googleapiclient";
    public static final String INTENT_FILTER_STEPS = "com.nosad.sample.steps";
    public static final String INTENT_FILTER_WARDS_COUNT = "com.nosad.sample.wardscount";
    public static final String INTENT_FILTER_UPDATE_UI = "com.nosad.sample.updateui";

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

    public final static String HOST =  "https://elbimmo.herokuapp.com";
    private final static String API_ADDRESS = HOST + "/api";

    public final static int CONNECTION_READ_TIMEOUT = 10000; // milliseconds
    public final static int CONNECTION_CONNECT_TIMEOUT = 15000; // milliseconds

    public final static String SOCKET_EVENT_LOCATION = "location";

    /**
     * RESTful APIs constants
     */
    public static String REST_API_USERS = HOST + "/users";

    /**
     * Exception constants
     */
    public static String EXCEPTION_FB_ACCESS_TOKEN_MISSING = "Facebook access token missing.";
}
