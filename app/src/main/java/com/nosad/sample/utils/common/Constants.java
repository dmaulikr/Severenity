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
        Experience("experience"),
        None("none");

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

    /**
     * Intent filter and intent extra constants
     */
    public static final String INTENT_FILTER_GAC = "com.nosad.sample.googleapiclient";
    public static final String INTENT_FILTER_WARDS_COUNT = "com.nosad.sample.wardscount";
    public static final String INTENT_FILTER_UPDATE_UI = "com.nosad.sample.updateui";
    public static final String INTENT_FILTER_NEW_MESSAGE = "com.nosad.sample.newmessage";
    public static final String INTENT_FILTER_NEW_QUEST = "com.nosad.sample.newquest";
    public static final String INTENT_FILTER_SHOW_PLACE_INFO_DIALOG = "com.nosad.sample.showplaceinfodialog";
    public static final String INTENT_FILTER_SHOW_PLACE_ACTIONS = "com.nosad.sample.showplaceactions";
    public static final String INTENT_FILTER_HIDE_PLACE_ACTIONS = "com.nosad.sample.hideplaceactions";

    public static final String INTENT_EXTRA_DEVICE_ID = "DEVICE_ID";
    public static final String INTENT_EXTRA_DEVICE_NAME = "DEVICE_NAME";

    public static final String EXTRA_GAC_CONNECTED = "isConnected";

    /**
     * Map related constants.
     */
    // Average running speed is:
    // from 4.7 meters/sec to 6.7 meters/sec (elite athletes)
    // Average walking speed is 1.4 meters/sec
    // We are assuming that people have walked at least for 10 seconds
    // and are not running better than elite athletes
    public static final int AVERAGE_WALKING_SPEED = 14; // meters for 10 seconds
    public static final int MAX_RUNNING_SPEED = 67; // meters for 10 seconds
    public static final int MIN_RUNNING_SPEED = 47; // meters for 10 seconds
    public static final int AVERAGE_RUNNING_SPEED = 57; // meters for 10 seconds
    public static final int EXPERIENCE_MULTIPLIER = 10;
    public static final int LEVEL_MULTIPLIER = 1000;

    public static final int INTERVAL_FAST_LOCATION_UPDATE = 1000;
    public static final int INTERVAL_LOCATION_UPDATE = 5000;

    public final static String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static Locale LOCALE = Locale.US;

    public final static float MIN_ZOOM_LEVEL = 20.0f;
    public final static float MAX_ZOOM_LEVEL = 17.0f;

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

    /**
     * Notification messages
     */
    public static String NOTIFICATION_MSG_NEW_QUEST = "New quest received! Accept the challenge?";

    /**
     * Gameplay constants
     */
    public static final float RESTORE_RATE_MENTALITY_NORMAL = 0.1f; // per day
    public static final float RESTORE_RATE_MENTALITY_MOVING = 1f;   // per 100 meters

    /**
     * PlaceInfo constants.
     * Used to pass specific data (as JSONobject) from  place to marker via snippet field.
     */
    public static final String OBJECT_INFO = "objectInfo";
    public static final String OBJECT_TYPE_IDENTIFIER = "placeType";
    public static final String PLACE_ID = "placeID";
    public static final String USER_ID  = "userID";

    public static final int    TYPE_USER  = 1;
    public static final int    TYPE_PLACE = 2;

}
