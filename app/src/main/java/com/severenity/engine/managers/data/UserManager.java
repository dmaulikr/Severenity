package com.severenity.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.severenity.App;
import com.severenity.engine.managers.messaging.GCMManager;
import com.severenity.engine.managers.messaging.RegistrationIntentService;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.User;
import com.severenity.utils.FacebookUtils;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_ACTION_RADIUS;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_CREATED_DATE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_CREDITS;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_DISTANCE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_EMAIL;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_ENERGY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_EXPERIENCE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_ID;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_IMMUNITY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_IMPLANT_HP;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_LEVEL;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_MAX_ENERGY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_MAX_IMMUNITY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_MAX_IMPLANT_HP;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_NAME;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_NULLABLE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_TEAM;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_VIEW_RADIUS;
import static com.severenity.entity.contracts.UserContract.DBUser.TABLE_USERS;

/**
 * Responsible for handling operations related to all users and current user.
 * Handles local database and appropriate APIs to the server.
 *
 * Created by Novosad on 2/17/16.
 */
public class UserManager extends DataManager {
    private User currentUser;

    public UserManager(Context context) {
        super(context);
    }

    public User addUser(User user) {
        User u = getUser(user);
        if (u != null) {
            return u;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.getId());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_CREATED_DATE, user.getCreatedDate());
        values.put(COLUMN_DISTANCE, user.getDistance());
        values.put(COLUMN_EXPERIENCE, user.getExperience());
        values.put(COLUMN_LEVEL, user.getLevel());
        values.put(COLUMN_IMMUNITY, user.getImmunity());
        values.put(COLUMN_ENERGY, user.getEnergy());
        values.put(COLUMN_MAX_IMMUNITY, user.getMaxImmunity());
        values.put(COLUMN_MAX_ENERGY, user.getMaxEnergy());
        values.put(COLUMN_CREDITS, user.getCredits());
        values.put(COLUMN_IMPLANT_HP, user.getImplantHP());
        values.put(COLUMN_MAX_IMPLANT_HP, user.getMaxImplantHP());
        values.put(COLUMN_ACTION_RADIUS, user.getActionRadius());
        values.put(COLUMN_VIEW_RADIUS, user.getViewRadius());
        values.put(COLUMN_TEAM, user.getTeam());

        long success = db.insert(TABLE_USERS, COLUMN_NULLABLE, values);
        db.close();

        if (success != -1) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * Returns user from local database based on id.
     *
     * @param id - id of the user to find.
     * @return - {@link User} user object if found, null otherwise.
     */
    public User getUserById(String id) {
        if (id == null || id.isEmpty()) {
            Log.e(Constants.TAG, "UserManager: user id specified in query must not be empty.");
            return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                "id = ?",
                new String[]{id},
                null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            user.setCreatedDate(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE)));
            user.setDistance(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_DISTANCE))));
            user.setExperience(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_EXPERIENCE))));
            user.setLevel(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_LEVEL))));
            user.setImmunity(cursor.getInt(cursor.getColumnIndex(COLUMN_IMMUNITY)));
            user.setMaxImmunity(cursor.getInt(cursor.getColumnIndex(COLUMN_MAX_IMMUNITY)));
            user.setEnergy(cursor.getInt(cursor.getColumnIndex(COLUMN_ENERGY)));
            user.setMaxEnergy(cursor.getInt(cursor.getColumnIndex(COLUMN_MAX_ENERGY)));
            user.setCredits(cursor.getInt(cursor.getColumnIndex(COLUMN_CREDITS)));
            user.setImplantHP(cursor.getInt(cursor.getColumnIndex(COLUMN_IMPLANT_HP)));
            user.setMaxImplantHP(cursor.getInt(cursor.getColumnIndex(COLUMN_MAX_IMPLANT_HP)));
            user.setViewRadius(cursor.getDouble(cursor.getColumnIndex(COLUMN_VIEW_RADIUS)));
            user.setActionRadius(cursor.getDouble(cursor.getColumnIndex(COLUMN_ACTION_RADIUS)));
            user.setTeam(cursor.getString(cursor.getColumnIndex(COLUMN_TEAM)));

            cursor.close();
            db.close();

            return user;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }

    /**
     * Returns user from local database based on {@link User} object.
     *
     * @param user - user to find.
     * @return - {@link User} user object if found, null otherwise.
     */
    public User getUser(User user) {
        if (checkIfNull(user)) {
            return getUserById(user.getId());
        } else {
            return null;
        }
    }

    /**
     * Updates current user.
     *
     * @param columns - specifies the array of columns to be updated
     * @param values  - specifies the value of the columns
     */
    public void updateCurrentUser(String[] columns, String[] values) {
        if (columns.length != values.length) {
            Log.w(Constants.TAG, "Inconsistent data for columns and values.");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues ctValues = new ContentValues();
        int i = 0;
        for (String column : columns) {
            ctValues.put(column, values[i]);
            i++;
        }

        db.update(TABLE_USERS, ctValues, "id = ?", new String[]{currentUser.getId()});
        db.close();

        setCurrentUser(retrieveCurrentUser());
    }

    /**
     * Updates current user locally based on data from user specified.
     *
     * @param user - {@link User} object to update current user data with.
     */
    public void updateCurrentUserLocallyWithUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISTANCE, user.getDistance());
        values.put(COLUMN_EXPERIENCE, user.getExperience());
        values.put(COLUMN_LEVEL, user.getLevel());
        values.put(COLUMN_ENERGY, user.getEnergy());
        values.put(COLUMN_MAX_ENERGY, user.getMaxEnergy());
        values.put(COLUMN_IMMUNITY, user.getImmunity());
        values.put(COLUMN_MAX_IMMUNITY, user.getMaxImmunity());
        values.put(COLUMN_IMPLANT_HP, user.getImplantHP());
        values.put(COLUMN_MAX_IMPLANT_HP, user.getMaxImplantHP());
        values.put(COLUMN_CREDITS, user.getCredits());
        values.put(COLUMN_TEAM, user.getTeam());

        if (currentUser != null && user.getLevel() > currentUser.getLevel()) {
            Intent levelUpIntent = new Intent(context, MainActivity.class);
            levelUpIntent.setAction(GCMManager.MESSAGE_RECEIVED);
            levelUpIntent.putExtra("level", String.valueOf(user.getLevel()));

            App.getLocalBroadcastManager().sendBroadcast(levelUpIntent);
            Utils.sendNotification(Constants.NOTIFICATION_MSG_LEVEL_UP + user.getLevel(), context, levelUpIntent, 0);
        }

        db.update(TABLE_USERS, values, "id = ?", new String[]{user.getId() == null ? currentUser.getId() : user.getId()});
        db.close();

        setCurrentUser(retrieveCurrentUser());
    }

    private User retrieveCurrentUser() {
        User user;
        if (AccessToken.getCurrentAccessToken() == null) {
            Log.i(Constants.TAG, "No access token found, new user is created.");
            user = new User();
        } else {
            user = getUserById(AccessToken.getCurrentAccessToken().getUserId());
        }
        return user;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            currentUser = retrieveCurrentUser();
        }
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Retrieves user from the server.
     *
     * @param userId - id of the user to retrieve
     */
    public void getUser(String userId, RequestCallback callback) {
        App.getRestManager().createRequest(Constants.REST_API_USERS + "/" + userId, Request.Method.GET, null, callback);
    }

    /**
     * Authorize user against the server.
     * If user does not exist - server will create a new one with Facebook ID provided.
     *
     * @param userId - id of the user to authorize or create.
     */
    public void authorizeUser(String userId, RequestCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            App.getRestManager().createRequest(Constants.REST_API_USERS, Request.Method.POST, data, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a request to create a speicified {@link User} on the server.
     *
     * @param user     - user to create on the server.
     * @param callback - callback to execute with response.
     */
    public void createUser(User user, RequestCallback callback) {
        JSONObject userObject = new JSONObject();
        try {
            userObject.put("userId", user.getId());
            userObject.put("name", user.getName());
            userObject.put("email", user.getEmail());

            App.getRestManager().createRequest(Constants.REST_API_CREATE_USER, Request.Method.POST, userObject, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the list of users from the server defined by range (pages)
     *
     * @param start - indicates start offset.
     * @param count - indicates count of records to be retrieved.
     */
    public void getUsersAsPage(int start, int count, RequestCallback callback) {

        String req = Constants.REST_API_USER_ALL_RANGE + "/?pageOffset=" + start + "&pageLimit=" + count;
        App.getRestManager().createRequest(req, Request.Method.GET, null, callback);
    }

    /**
     * Sends distance passed update to the server in order to update experience,
     * distance and level.
     *
     * @param userId       - id of the user which has to be updated.
     * @param metersPassed - last meters passed update.
     */
    public void updateCurrentUserProgress(String userId, int metersPassed) {
        try {
            JSONObject data = new JSONObject();
            data.put("userId", userId);
            data.put("field", "distance");
            data.put("amount", metersPassed);
            App.getWebSocketManager().sendUserUpdateToServer(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void authorizeCurrentUser() {
        App.getUserManager().authorizeUser(AccessToken.getCurrentAccessToken().getUserId(), new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                Intent intent = new Intent(Constants.INTENT_FILTER_AUTHENTICATION);
                try {
                    String result = response.getString("result");
                    intent.putExtra("result", result);

                    switch (result) {
                        case "success":
                            final JSONObject userObject = response.getJSONObject("user");
                            JSONArray devices = userObject.getJSONArray("devices");
                            String userId = userObject.getString("userId");

                            // If device was not registered to the user - start registration service
                            if (devices.length() == 0) {
                                startDeviceRegistrationService(userId);
                            } else {
                                if (!checkDeviceRegistrationToken(devices.getJSONObject(0).getString("registrationId"))) {
                                    startDeviceRegistrationService(userId);
                                    return;
                                }

                                App.getUserManager().createCurrentUserAndNotify(userObject, intent);
                            }
                            break;
                        case "continue":
                            if (response.getInt("reason") == 1) {
                                createUser();
                            } else {
                                Log.e(Constants.TAG, "Unknown reason value.");
                            }
                            App.getLocalBroadcastManager().sendBroadcast(intent);
                            break;
                        case "error":
                            Log.e(Constants.TAG, "Error handling is not implemented yet.");
                            App.getLocalBroadcastManager().sendBroadcast(intent);
                            break;
                        default:
                            Log.e(Constants.TAG, "Unknown result value.");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    intent.putExtra("result", "error");
                    App.getLocalBroadcastManager().sendBroadcast(intent);
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                Log.e(Constants.TAG, response != null ? response.toString() : "Response is null");
            }
        });
    }

    /**
     * Triggers user creation on the server and stores
     * newly created user in local db.
     */
    private void createUser() {
        FacebookUtils.getFacebookUserById(AccessToken.getCurrentAccessToken().getUserId(), "id,name,email", new FacebookUtils.Callback() {
            @Override
            public void onResponse(GraphResponse response) {
                final User user = new User();
                user.setId(AccessToken.getCurrentAccessToken().getUserId());
                try {
                    JSONObject data = response.getJSONObject();
                    if (data.has("name") && data.has("id")) {
                        if (data.has("email")) {
                            user.setEmail(data.getString("email"));
                        }

                        user.setName(data.getString("name"));

                        App.getUserManager().createUser(user, new RequestCallback() {
                            @Override
                            public void onResponseCallback(JSONObject response) {
                                if (response != null) {
                                    Log.d(Constants.TAG, response.toString());

                                    User newUser = Utils.createUserFromJSON(response);
                                    if (newUser != null) {
                                        App.getUserManager().setCurrentUser(newUser);
                                        authorizeCurrentUser();
                                    }
                                } else {
                                    Log.e(Constants.TAG, "User create has null response.");
                                }
                            }

                            @Override
                            public void onErrorCallback(NetworkResponse response) {
                                if (response != null) {
                                    Log.e(Constants.TAG, response.toString());
                                } else {
                                    Log.e(Constants.TAG, "User create error has null response.");
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Starts {@link RegistrationIntentService} to register current device.
     *
     * @param userId - id of the user to bind device to.
     */
    private void startDeviceRegistrationService(String userId) {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        intent.putExtra(Constants.INTENT_EXTRA_DEVICE_ID, Utils.getDeviceId(context));
        intent.putExtra(Constants.INTENT_EXTRA_DEVICE_NAME, Utils.getDeviceName());
        intent.putExtra(Constants.INTENT_EXTRA_USER_ID, userId);
        intent.putExtra(Constants.INTENT_EXTRA_REGISTRATION_ID, App.getCurrentFCMToken());
        context.startService(intent);
    }

    /**
     * Checks current device token against one used on the server.
     *
     * @param token - token of the current device assigned to user
     * @return true if token is the same as current, false otherwise.
     */
    private boolean checkDeviceRegistrationToken(String token) {
        return token.equals(App.getCurrentFCMToken());
    }

    /**
     * Creates current user in local db or updates it depending on response.
     *
     * @param userObject - {@link JSONObject} object for the user to create.
     */
    public void createCurrentUserAndNotify(JSONObject userObject, Intent intent) {
        User user = Utils.createUserFromJSON(userObject);
        if (App.getUserManager().getUser(user) != null) {
            updateCurrentUserLocallyWithUser(user);
        } else {
            setCurrentUser(App.getUserManager().addUser(user));
        }
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }
}
