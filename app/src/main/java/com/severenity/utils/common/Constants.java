package com.severenity.utils.common;

import java.util.Locale;

/**
 * Created by Novosad on 02/06/2015.
 */
public class Constants {
    private Constants() {
        // Added to hide explicit public constructor.
    }

    public enum Characteristic {
        Experience("experience"),
        Level("level"),
        Credits("credits"),
        Energy("energy"),
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

    public enum UsersActions {
        CAPTURE("capture"),     // user captures the building
        ATTACK("attack"),       // user attacks other user
        REMOVE("remove"),       // user removes other user from owning place
        ATTACKED("attacked"),   // user was attached by other user
        RECOVERY("recovery");   // recovery user energy due to time/distance passed

        private final String action;

        UsersActions(String action) {
            this.action = action;
        }

        public String getAction() {
            return this.action;
        }
    }

    public enum ObjectTypes {
        USER("user"),
        PLACE("place");

        private final String type;

        ObjectTypes(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

    public final static String TAG = "SEVERENITY";

    /**
     * Intent filter and intent extra constants
     */
    public static final String INTENT_FILTER_GAC = "com.severenity.googleapiclient";
    public static final String INTENT_FILTER_UPDATE_UI = "com.severenity.updateui";
    public static final String INTENT_FILTER_NEW_MESSAGE = "com.severenity.newmessage";
    public static final String INTENT_FILTER_NEW_QUEST = "com.severenity.newquest";
    public static final String INTENT_FILTER_QUEST_UPDATE = "com.severenity.questupdate";
    public static final String INTENT_FILTER_SHOW_PLACE_INFO_DIALOG = "com.severenity.showplaceinfodialog";
    public static final String INTENT_FILTER_SHOW_USER_ACTIONS = "com.severenity.showplaceactions";
    public static final String INTENT_FILTER_HIDE_USER_ACTIONS = "com.severenity.hideplaceactions";
    public static final String INTENT_FILTER_DELETE_OWNER = "com.severenity.deleteowner";
    public static final String INTENT_FILTER_REQUEST_PLACES = "com.severenity.requestplaces";
    public static final String INTENT_FILTER_AUTHENTICATION = "com.severenity.authentication";
    public static final String INTENT_FILTER_UPDATE_STATUS_LABEL = "com.severenity.updatestatuslabel";
    public static final String INTENT_FILTER_TEAM_CHANGED = "com.severenity.teamchanged";

    public static final String INTENT_EXTRA_USER_ID = "USER_ID";
    public static final String INTENT_EXTRA_DEVICE_ID = "DEVICE_ID";
    public static final String INTENT_EXTRA_DEVICE_NAME = "DEVICE_NAME";
    public static final String INTENT_EXTRA_REGISTRATION_ID = "REGISTRATION_ID";
    public static final String INTENT_EXTRA_SINGLE_QUEST = "SINGLE_QUEST";
    public static final String INTENT_EXTRA_SHOW_TEAM_QUESTS = "SHOW_TEAM_QUESTS";

    public static final String EXTRA_GAC_CONNECTED = "isConnected";

    /**
     * Map related constants.
     */
    // Average running speed is:
    // from 4.7 meters/sec to 6.7 meters/sec (elite athletes)
    // Average walking speed is 1.4 meters/sec, lets take 1.2 meters for calm walk.
    // We are assuming that people have walked at least for 10 seconds
    // and are not running better than elite athletes
    public static final int AVERAGE_WALKING_SPEED = 12; // meters for 10 seconds
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
    public final static float MAX_ZOOM_LEVEL = 18.0f;

    /**
     * Connection constants
     */

    public final static String HOST =  "https://severenity.herokuapp.com";
//    public final static String HOST =  "http://localhost:8080";

    public final static int CONNECTION_READ_TIMEOUT = 10000; // milliseconds
    public final static int CONNECTION_CONNECT_TIMEOUT = 15000; // milliseconds

    public final static String SOCKET_EVENT_LOCATION = "location";
    public final static String SOCKET_EVENT_MESSAGE = "chat message";
    public final static String SOCKET_EVENT_UPDATE_PLACE = "update place";
    public final static String SOCKET_EVENT_UPDATE_USER = "update user";
    public final static String SOCKET_EVENT_AUTHENTICATE = "authenticate";
    public final static String SOCKET_EVENT_USER_ACTION  = "user action";

    /**
     * RESTful APIs constants
     */
    public static String REST_API_USERS = HOST + "/users";
    public static String REST_API_CREATE_USER = HOST + "/users/create";
    public static String REST_API_DEVICES = "/devices";
    public static String REST_API_SEND = HOST + "/send";
    public static String REST_API_PLACES = HOST + "/places";
    public static String REST_API_QUESTS = "/quests";
    public static String REST_API_QUESTS_UPDATE = "/quests/update";
    public static String REST_API_PLACES_ALL = HOST + "/places/all";
    public static String REST_API_USER_ALL = HOST + "/users/all";
    public static String REST_API_TEAM_CREATE  = HOST + "/teams/create";
    public static String REST_API_TEAMS  = HOST + "/teams";
    public static String REST_API_TEAM_REMOVE_USER  = HOST + "/teams/removeUser";
    public static String REST_API_TEAM_JOIN_TEAM  = HOST + "/teams/join";
    public static String REST_API_MESSAGES = HOST + "/messages/all";
    public static String REST_API_TEAM_QUESTS = HOST + "/teamQuests";

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
    public static String NOTIFICATION_MSG_LEVEL_UP = "Greetings! You have reached level";

    /**
     * Gameplay constants
     */
    public static final float RESTORE_RATE_MENTALITY_NORMAL = 0.1f; // per day
    public static final float RESTORE_RATE_MENTALITY_MOVING = 1f;   // per 100 meters

    /**
     * PlaceInfo constants.
     * Used to pass specific data (as {@link org.json.JSONObject}) via markers spinner object.
     */
    public static final String OBJECT_INFO_AS_JSON = "objectInfo";
    public static final String OBJECT_TYPE_IDENTIFIER = "placeType";
    public static final String PLACE_ID = "placeID";
    public static final String USER_ID  = "userID";

    public static final int    TYPE_USER  = 1;
    public static final int    TYPE_PLACE = 2;

    /**
     * User action and view circles parameters
     */
    public static final int VIEW_CIRCLE_STOKE_COLOR = 0xeecccccc;
    public static final int VIEW_CIRCLE_SHADE_COLOR = 0x22cccccc;
    public static final int VIEW_CIRCLE_BORDER_SIZE = 2;

    public static final int ACTION_CIRCLE_STOKE_COLOR = 0xff333333;
    public static final int ACTION_CIRCLE_SHADE_COLOR = 0x22333333;
    public static final int ACTION_CIRCLE_BORDER_SIZE = 2;

    /**
     *
     */
    public static final double EARTH_CIRCUMFERENCE = 40075.04;
    public static final int EAST_DIRECTION  = 0;
    public static final int WEST_DIRECTION  = 1;
    public static final int NORTH_DIRECTION = 2;
    public static final int SOUTH_DIRECTION = 3;
    public static final int EN_DIRECTION    = 4;
    public static final int ES_DIRECTION    = 5;
    public static final int WN_DIRECTION    = 6;
    public static final int WS_DIRECTION    = 7;

    public static final int DISTANCE_TO_UPDATE_POSITIONS_CONSTS = 5;
    public static final int DISTANCE_TO_REQUEST_NEW_PLACES_FROM_THE_SERVER = 100;

    public static final int DISTANCE_TO_PASS_FOR_RECOVERY = 100;
    public static final int DAYS_TO_PASS_FOR_RECOVERY     = 1;
}
