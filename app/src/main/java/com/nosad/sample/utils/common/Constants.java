package com.nosad.sample.utils.common;

/**
 * Created by Novosad on 02/06/2015.
 */
public class Constants {
    public final static String TAG = "SAMPLE";

    public static final String INTENT_FILTER_GAC = "com.nosad.sample.googleapiclient";
    public static final String INTENT_FILTER_STEPS = "com.nosad.sample.steps";
    public static final String INTENT_FILTER_WARDS_COUNT = "com.nosad.sample.wardscount";

    public static final String EXTRA_GAC_CONNECTED = "isConnected";
    public static final String EXTRA_STEPS = "steps";

    /**
     * Connection constants
     */

    private final static String HOST =  "localhost:3000/api";
    public static String WS_ADDRESS = "ws://" + HOST + "/game";

    public final static int CONNECTION_READ_TIMEOUT = 10000; // milliseconds
    public final static int CONNECTION_CONNECT_TIMEOUT = 15000; // milliseconds

    /**
     * RESTful APIs constants
     */
    public static String REST_API_USERS = "http://"  + HOST + "/users";

    /**
     * Exception constants
     */
    public static String EXCEPTION_FB_ACCESS_TOKEN_MISSING = "Facebook access token missing.";
}
