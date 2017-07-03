package com.severenity.engine.managers.data;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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

    public User addUser(final User user) {
        User u = getUser(user);
        if (u != null) {
            return u;
        }

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(user);
                }
            });

            return realm.where(User.class).equalTo("id", user.getId()).findFirst();
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

        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("id", id).findFirst();
        realm.close();

        return user;
    }

    /**
     * Returns user from local database based on {@link User} object.
     *
     * @param user - user to find.
     * @return - {@link User} user object if found, null otherwise.
     */
    private User getUser(User user) {
        if (checkIfNull(user)) {
            return getUserById(user.getId());
        } else {
            return null;
        }
    }

    /**
     * Returns all users currently stored on the device.
     *
     * @return list of users.
     */
    public List<User> getUsers() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<User> realmResults = realm.where(User.class).findAll();
            return realm.copyFromRealm(realmResults);
        }
    }

    /**
     * Updates current user team.
     *
     * @param teamId - new id of the team
     */
    public void updateCurrentUserTeam(final String teamId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.where(User.class).equalTo("id", getCurrentUser().getId()).findFirst();
                    user.setTeamId(teamId);
                    realm.copyToRealmOrUpdate(user);
                }
            });
        }
    }

    /**
     * Updates current user locally based on data from user specified.
     *
     * @param u - {@link User} object to update current user data with.
     */
    public void updateCurrentUserLocallyWithUser(final User u) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User currentUser = realm.where(User.class).equalTo("id", u.getId()).findFirst();
                    currentUser.setActionRadius(u.getActionRadius());
                    currentUser.setViewRadius(u.getViewRadius());
                    currentUser.setCredits(u.getCredits());
                    currentUser.setMaxEnergy(u.getMaxEnergy());
                    currentUser.setEnergy(u.getEnergy());
                    currentUser.setExperience(u.getExperience());
                    currentUser.setDistance(u.getDistance());
                    currentUser.setTickets(u.getTickets());
                    currentUser.setTips(u.getTips());

                    if (u.getLevel() > currentUser.getLevel()) {
                        Intent levelUpIntent = new Intent(context, MainActivity.class);
                        levelUpIntent.setAction(GCMManager.MESSAGE_RECEIVED);
                        levelUpIntent.putExtra("level", String.valueOf(u.getLevel()));

                        App.getLocalBroadcastManager().sendBroadcast(levelUpIntent);
                        Utils.sendNotification(Constants.NOTIFICATION_MSG_LEVEL_UP + u.getLevel(), context, levelUpIntent, 0);
                    }

                    currentUser.setLevel(u.getLevel());

                    realm.copyToRealmOrUpdate(currentUser);
                }
            });
        }
    }

    public User getCurrentUser() {
        return currentUser;
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
    private void authorizeUser(String userId, RequestCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            App.getRestManager().createRequest(Constants.REST_API_USERS, Request.Method.POST, data, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a request to create a specified {@link User} on the server.
     *
     * @param user     - user to create on the server.
     * @param callback - callback to execute with response.
     */
    private void createUser(User user, RequestCallback callback) {
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
        String req = Constants.REST_API_USERS + "?pageOffset=" + start + "&pageLimit=" + count;
        App.getRestManager().createRequest(req, Request.Method.GET, null, callback);
    }

    /**
     * Sends field update for the user with specified value.
     *
     * @param field - field to update.
     * @param value - amount to add.
     */
    public void updateCurrentUser(String field, int value) {
        try {
            JSONObject data = new JSONObject();
            data.put("userId", getCurrentUser().getId());
            data.put("field", field);
            data.put("amount", value);
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

                                createCurrentUserAndNotify(userObject, intent);
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
                                    if (addUser(newUser) != null) {
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
        intent.putExtra(Constants.INTENT_EXTRA_DEVICE_ID, Utils.getDeviceId());
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
        currentUser = getUser(user);
        if (currentUser != null) {
            updateCurrentUserLocallyWithUser(user);
        } else {
            currentUser = addUser(user);
        }

        App.getLocalBroadcastManager().sendBroadcast(intent);
    }
}
