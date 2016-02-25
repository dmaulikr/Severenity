package com.nosad.sample.utils.common;

/**
 * Created by Novosad on 02/06/2015.
 */
public class Constants {
    public final static String TAG = "SAMPLE";
    public final static int SPLASH_TIME_OUT = 3000;

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
}
