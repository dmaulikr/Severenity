package com.nosad.sample.common;

/**
 * Created by Novosad on 02/06/2015.
 */
public class Constants {
    private final static String HOST =  "localhost:8080/Sample_war_exploded";
    public final static int SPLASH_TIME_OUT = 3000;

    public static String TAG = "SAMPLE";
    public static String WS_ADDRESS = "ws://" + HOST + "/game";

    /**
     * RESTful APIs constants
     */
    public static String REST_API_AUTH = "http://"  + HOST + "/rest/auth";
}
