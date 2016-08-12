package com.severenity.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.facebook.AccessToken;
import com.severenity.App;
import com.severenity.engine.managers.messaging.GCMManager;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.User;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_ACTION_RADIUS;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_CREATED_DATE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_CREDITS;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_DISTANCE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_EMAIL;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_EXPERIENCE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_ID;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_IMMUNITY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_IMPLANT_HP;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_ENERGY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_LEVEL;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_MAX_IMMUNITY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_MAX_ENERGY;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_MAX_IMPLANT_HP;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_NAME;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_NULLABLE;
import static com.severenity.entity.contracts.UserContract.DBUser.COLUMN_VIEW_RADIUS;
import static com.severenity.entity.contracts.UserContract.DBUser.TABLE_USERS;

/**
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

        long success = db.insert(TABLE_USERS, COLUMN_NULLABLE, values);
        db.close();

        if (success != -1) {
            return user;
        } else {
            return null;
        }
    }

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

    public User getUser(User user) {
        if (checkIfNull(user)) {
            return getUserById(user.getId());
        } else {
            return null;
        }
    }

    public void updateCurrentUserLocally() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISTANCE, currentUser.getDistance());
        values.put(COLUMN_EXPERIENCE, currentUser.getExperience());
        values.put(COLUMN_LEVEL, currentUser.getLevel());
        values.put(COLUMN_ENERGY, currentUser.getEnergy());
        values.put(COLUMN_MAX_ENERGY, currentUser.getMaxEnergy());
        values.put(COLUMN_IMMUNITY, currentUser.getImmunity());
        values.put(COLUMN_MAX_IMMUNITY, currentUser.getMaxImmunity());
        values.put(COLUMN_IMPLANT_HP, currentUser.getImplantHP());
        values.put(COLUMN_MAX_IMPLANT_HP, currentUser.getMaxImplantHP());
        values.put(COLUMN_CREDITS, currentUser.getCredits());

        db.update(TABLE_USERS, values, "id = ?", new String[]{currentUser.getId()});
        db.close();

        retrieveCurrentUser();
    }

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

        if (currentUser != null && user.getLevel() > currentUser.getLevel()) {
            Intent levelUpIntent = new Intent(context, MainActivity.class);
            levelUpIntent.setAction(GCMManager.MESSAGE_RECEIVED);
            levelUpIntent.putExtra("level", String.valueOf(user.getLevel()));

            App.getLocalBroadcastManager().sendBroadcast(levelUpIntent);
            Utils.sendNotification(Constants.NOTIFICATION_MSG_LEVEL_UP + user.getLevel(), context, levelUpIntent, 0);
        }

        db.update(TABLE_USERS, values, "id = ?", new String[]{ user.getId() == null ? currentUser.getId() : user.getId() });
        db.close();

        retrieveCurrentUser();
    }

    private void retrieveCurrentUser() {
        if (AccessToken.getCurrentAccessToken() == null) {
            Log.i(Constants.TAG, "No access token found, new user is created.");
            currentUser = new User();
        } else {
            currentUser = getUserById(AccessToken.getCurrentAccessToken().getUserId());
        }
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            retrieveCurrentUser();
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
     * @param user - user to create on the server.
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
     * @param sortby - indicate user's filed to sort by.
     */
    public void getUsersAsPage(int start, int count, String sortby, RequestCallback callback) {
        JSONObject request = new JSONObject();

        try {
            request.put("pageOffset", start);
            request.put("pageLimit", count);
            request.put("sortby", sortby);

            App.getRestManager().createRequest(Constants.REST_API_USER_ALL_RANGE, Request.Method.POST, request, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends distance passed update to the server in order to update experience,
     * distance and level.
     *
     * @param userId - id of the user which has to be updated.
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
}
