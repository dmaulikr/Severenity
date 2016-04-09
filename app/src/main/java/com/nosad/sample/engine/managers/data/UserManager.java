package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.common.Constants;

import static com.nosad.sample.entity.contracts.UserContract.DBUser.COLUMN_EMAIL;
import static com.nosad.sample.entity.contracts.UserContract.DBUser.COLUMN_EXPERIENCE;
import static com.nosad.sample.entity.contracts.UserContract.DBUser.COLUMN_ID;
import static com.nosad.sample.entity.contracts.UserContract.DBUser.COLUMN_LEVEL;
import static com.nosad.sample.entity.contracts.UserContract.DBUser.COLUMN_NAME;
import static com.nosad.sample.entity.contracts.UserContract.DBUser.COLUMN_NULLABLE;
import static com.nosad.sample.entity.contracts.UserContract.DBUser.COLUMN_STEPS;
import static com.nosad.sample.entity.contracts.UserContract.DBUser.TABLE_USERS;

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
        values.put(COLUMN_STEPS, user.getSteps());
        values.put(COLUMN_EXPERIENCE, user.getExperience());
        values.put(COLUMN_LEVEL, user.getLevel());

        long success = db.insert(TABLE_USERS, COLUMN_NULLABLE, values);
        db.close();

        return success == -1 ? null : user;
    }

    public User getUserById(String id) {
        if (id == null || id.isEmpty()) {
            Log.e(Constants.TAG, "UserManager: user id specified in query must not be empty.");
            return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_STEPS},
                "id = ?",
                new String[]{id},
                null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            String steps = cursor.getString(cursor.getColumnIndex(COLUMN_STEPS));
            if (steps != null) {
                user.setSteps(Integer.valueOf(steps));
            }

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
        checkIfNull(user);

        return getUserById(user.getId());
    }

    public void deleteUser(User user) {
        checkIfNull(user);

        deleteUserById(user.getId());
    }

    public void deleteUserById(String id) {
        if (id == null || id.isEmpty()) {
            Log.e(Constants.TAG, "UserManager: user id specified in query must not be empty.");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_USERS, "id = ?", new String[]{id});
        db.close();
    }

    public void deleteAllUsers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.close();
    }

    public void updateUserInfo() {
        User user = getCurrentUser();

        if (user == null) {
            Log.e(Constants.TAG,
                "User with access token: "
                + AccessToken.getCurrentAccessToken() +
                " is not created yet"
            );
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STEPS, user.getSteps());
        values.put(COLUMN_EXPERIENCE, user.getExperience());
        values.put(COLUMN_LEVEL, user.getLevel());

        db.update(TABLE_USERS, values, "id = ?", new String[]{user.getId()});
        db.close();
    }

    public User getCurrentUser(){
        if (currentUser == null) {
            if (AccessToken.getCurrentAccessToken() == null) {
                Log.i(Constants.TAG, "No access token found, new user is created.");
                currentUser = new User();
            } else {
                currentUser = getUserById(AccessToken.getCurrentAccessToken().getUserId());
            }
        }
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }
}
